package com.ayudaz.ayudaz_backend.controller;

import com.ayudaz.ayudaz_backend.dto.AyudadoRegistroDTO;
import com.ayudaz.ayudaz_backend.dto.RegistroDTO;
import com.ayudaz.ayudaz_backend.model.*;
import com.ayudaz.ayudaz_backend.repository.AyudadoRepository;
import com.ayudaz.ayudaz_backend.service.UsuarioService;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseToken;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;

@RestController
@RequestMapping("/api/auth")
@CrossOrigin(origins = "*")
public class AuthController {


    @Autowired
    private UsuarioService usuarioService;


    @Autowired
    private AyudadoRepository ayudadoRepository;



    // ==========================================================
    // VERIFY FIREBASE
    // ==========================================================

    @PostMapping("/verify")
    @Transactional
    public ResponseEntity<?> verifyToken(
            @RequestHeader("Authorization") String authHeader
    ){

        try {


            FirebaseToken decoded = validarFirebase(authHeader);


            Usuario usuario = obtenerOCrearUsuario(decoded);



            usuario.setUltimoAcceso(LocalDateTime.now());

            usuarioService.save(usuario);



            return ResponseEntity.ok(usuario);



        }catch(Exception e){

            return ResponseEntity
                    .status(401)
                    .body("Token inválido: " + e.getMessage());

        }

    }



    // ==========================================================
    // REGISTRO VOLUNTARIO
    // ==========================================================


    @PostMapping("/registro/voluntario")
    @Transactional
    public ResponseEntity<?> registrarVoluntario(
            @RequestBody RegistroDTO dto,
            @RequestHeader("Authorization") String authHeader
    ){

        try {

            FirebaseToken firebase = validarFirebase(authHeader);


            Usuario usuario = obtenerOCrearUsuario(firebase);


            usuario.setNombre(dto.getNombre());

            // mantener correo de Firebase
            if(dto.getEmail() != null && !dto.getEmail().isEmpty()){
                usuario.setEmail(dto.getEmail());
            }


            usuario.setTipoUsuario(TipoUsuario.voluntario);
            usuario.setEstado(EstadoUsuario.activo);
            usuario.setUltimoAcceso(LocalDateTime.now());


            Usuario guardado = usuarioService.save(usuario);


            return ResponseEntity.ok(guardado);


        } catch(Exception e){

            e.printStackTrace();

            return ResponseEntity
                    .badRequest()
                    .body(
                            "Error al registrar voluntario: "
                                    + e.getMessage()
                    );
        }
    }



    // ==========================================================
    // REGISTRO AYUDADO
    // ==========================================================


    @Transactional
    @PostMapping("/registro/ayudado")
    public ResponseEntity<?> registrarAyudado(
            @RequestBody AyudadoRegistroDTO dto,
            @RequestHeader("Authorization") String authHeader) {

        try {

            String uid = verificarToken(authHeader);


            // Buscar usuario creado por /verify
            Usuario usuario = usuarioService.findByFirebaseUid(uid)
                    .orElseThrow(() ->
                            new RuntimeException(
                                    "Usuario no encontrado. Primero debe ejecutar verify"
                            )
                    );


            // Solo actualizar datos del usuario existente
            usuario.setNombre(dto.getNombre());
            usuario.setEmail(dto.getEmail());
            usuario.setTipoUsuario(TipoUsuario.ayudado);
            usuario.setEstado(EstadoUsuario.pendiente);
            usuario.setUltimoAcceso(LocalDateTime.now());


            usuario = usuarioService.save(usuario);



            // Crear perfil ayudado
            Ayudado ayudado = ayudadoRepository
                    .findByUsuarioId(usuario.getId())
                    .orElse(new Ayudado());


            ayudado.setUsuario(usuario);

            ayudado.setDni(dto.getDni());

            ayudado.setNombreConyuge(
                    dto.getNombreConyuge()
            );

            ayudado.setFechaNacimientoConyuge(
                    dto.getFechaNacimientoConyuge()
            );

            ayudado.setLugarNacimientoConyuge(
                    dto.getLugarNacimientoConyuge()
            );

            ayudado.setCantidadIntegrantes(
                    dto.getCantidadIntegrantes()
            );


            ayudado.setConfirmacionAdmin(false);


            ayudadoRepository.save(ayudado);



            return ResponseEntity.ok(
                    "Solicitud enviada correctamente. Pendiente de revisión."
            );


        } catch(Exception e){

            e.printStackTrace();

            return ResponseEntity.badRequest()
                    .body(
                            "Error al registrar ayudado: "
                                    + e.getMessage()
                    );
        }
    }


    // ==========================================================
    // CREA O BUSCA USUARIO
    // ==========================================================


    private synchronized Usuario obtenerOCrearUsuario(
            FirebaseToken firebase
    ){

        String uid = firebase.getUid();

        return usuarioService
                .findByFirebaseUid(uid)
                .orElseGet(() -> {


                    Usuario nuevo = new Usuario();


                    nuevo.setFirebaseUid(uid);

                    nuevo.setEmail(firebase.getEmail());

                    nuevo.setNombre(
                            firebase.getName()!=null
                                    ?
                                    firebase.getName()
                                    :
                                    firebase.getEmail()
                                            .split("@")[0]
                    );


                    nuevo.setEstado(
                            EstadoUsuario.pendiente
                    );


                    nuevo.setFechaRegistro(
                            LocalDateTime.now()
                    );


                    return usuarioService.save(nuevo);


                });

    }



    // ==========================================================
    // VALIDAR FIREBASE TOKEN
    // ==========================================================


    private FirebaseToken validarFirebase(
            String authHeader
    ) throws Exception{


        if(authHeader==null ||
                !authHeader.startsWith("Bearer ")){

            throw new Exception(
                    "Token no enviado"
            );

        }


        String token =
                authHeader.substring(7);



        return FirebaseAuth
                .getInstance()
                .verifyIdToken(token);

    }

    // ── Helper: verifica token Firebase y devuelve UID ─────────────────────
    private String verificarToken(String authHeader) throws Exception {

        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            throw new RuntimeException("Token no enviado");
        }

        String token = authHeader.replace("Bearer ", "");

        FirebaseToken decoded =
                FirebaseAuth.getInstance()
                        .verifyIdToken(token);

        return decoded.getUid();
    }
}