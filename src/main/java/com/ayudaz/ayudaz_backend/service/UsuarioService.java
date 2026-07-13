package com.ayudaz.ayudaz_backend.service;

import com.ayudaz.ayudaz_backend.model.Ayudado;
import com.ayudaz.ayudaz_backend.model.EstadoUsuario;
import com.ayudaz.ayudaz_backend.model.TipoUsuario;
import com.ayudaz.ayudaz_backend.model.Usuario;
import com.ayudaz.ayudaz_backend.repository.AyudadoRepository;
import com.ayudaz.ayudaz_backend.repository.UsuarioRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class UsuarioService {


    @Autowired
    private UsuarioRepository usuarioRepository;


    @Autowired
    private AyudadoRepository ayudadoRepository;



    /**
     * Busca usuario por Firebase UID
     */
    public Optional<Usuario> findByFirebaseUid(String firebaseUid){

        return usuarioRepository.findByFirebaseUid(firebaseUid);

    }



    /**
     * Crea o actualiza usuario desde Firebase
     */
    @Transactional
    public synchronized Usuario registrarOActualizarUsuario(
            String firebaseUid,
            String email,
            String nombre,
            String tipoUsuario
    ){


        Optional<Usuario> encontrado =
                usuarioRepository.findByFirebaseUid(firebaseUid);



        Usuario usuario;


        if(encontrado.isPresent()){


            usuario = encontrado.get();

            usuario.setUltimoAcceso(
                    LocalDateTime.now()
            );


            if(email != null)
                usuario.setEmail(email);


            if(nombre != null)
                usuario.setNombre(nombre);



        }else{


            usuario = new Usuario();


            usuario.setFirebaseUid(firebaseUid);

            usuario.setEmail(email);

            usuario.setNombre(nombre);


            usuario.setEstado(
                    EstadoUsuario.pendiente
            );


            usuario.setFechaRegistro(
                    LocalDateTime.now()
            );


            if(tipoUsuario != null){

                usuario.setTipoUsuario(
                        TipoUsuario.valueOf(tipoUsuario)
                );

            }


        }


        return usuarioRepository.save(usuario);

    }





    /**
     * Método general de guardado
     * evita duplicados por Firebase UID
     */
    @Transactional
    public synchronized Usuario save(Usuario usuario){


        if(usuario.getFirebaseUid()!=null){


            Optional<Usuario> existente =
                    usuarioRepository
                            .findByFirebaseUid(
                                    usuario.getFirebaseUid()
                            );



            if(existente.isPresent()){


                Usuario actual =
                        existente.get();


                usuario.setId(
                        actual.getId()
                );

            }

        }


        return usuarioRepository.save(usuario);

    }





    public List<Usuario> getUsuariosPendientes(){

        return usuarioRepository
                .findByEstado(
                        EstadoUsuario.pendiente
                );

    }




    @Transactional
    public void aprobarUsuario(Long usuarioId){


        Usuario usuario =
                usuarioRepository.findById(usuarioId)
                        .orElseThrow(
                                ()->new RuntimeException(
                                        "Usuario no encontrado"
                                )
                        );



        usuario.setEstado(
                EstadoUsuario.activo
        );


        usuarioRepository.save(usuario);



        if(usuario.getTipoUsuario()
                == TipoUsuario.ayudado){



            ayudadoRepository
                    .findByUsuarioId(usuarioId)
                    .ifPresent(ayudado -> {


                        ayudado.setConfirmacionAdmin(true);

                        ayudado.setFechaConfirmacion(
                                LocalDateTime.now()
                        );


                        ayudadoRepository.save(ayudado);


                    });

        }

    }




    @Transactional
    public void eliminarUsuario(Long usuarioId){

        usuarioRepository.deleteById(usuarioId);

    }




    @Transactional
    public Ayudado guardarDatosAyudado(
            Usuario usuario,
            Ayudado datosAyudado
    ){

        datosAyudado.setUsuario(usuario);

        return ayudadoRepository.save(datosAyudado);

    }





    public Optional<Usuario> findById(Long id){

        return usuarioRepository.findById(id);

    }




    public List<Usuario> findAll(){

        return usuarioRepository.findAll();

    }




    @Transactional
    public Usuario crearUsuario(Usuario usuario){

        usuario.setTipoUsuario(null);

        return save(usuario);

    }





    @Transactional
    public Usuario actualizarUsuario(
            Usuario usuario
    ){


        if(!usuarioRepository.existsById(usuario.getId())){

            throw new RuntimeException(
                    "Usuario no encontrado para actualizar"
            );

        }


        return usuarioRepository.save(usuario);

    }





    @Transactional
    public void suspenderUsuario(Long usuarioId){


        Usuario usuario =
                usuarioRepository.findById(usuarioId)
                        .orElseThrow(
                                ()->new RuntimeException(
                                        "Usuario no encontrado"
                                )
                        );


        usuario.setEstado(
                EstadoUsuario.rechazado
        );


        usuarioRepository.save(usuario);

    }





    @Transactional
    public Usuario editarUsuario(
            Long usuarioId,
            String nombre,
            String email,
            String tipoUsuario,
            String estado
    ){


        Usuario usuario =
                usuarioRepository.findById(usuarioId)
                        .orElseThrow(
                                ()->new RuntimeException(
                                        "Usuario no encontrado"
                                )
                        );



        if(nombre!=null)
            usuario.setNombre(nombre);



        if(email!=null)
            usuario.setEmail(email);



        if(tipoUsuario!=null)
            usuario.setTipoUsuario(
                    TipoUsuario.valueOf(tipoUsuario)
            );



        if(estado!=null)
            usuario.setEstado(
                    EstadoUsuario.valueOf(estado)
            );



        return usuarioRepository.save(usuario);

    }





    @Transactional
    public void activarUsuario(Long usuarioId){


        Usuario usuario =
                usuarioRepository.findById(usuarioId)
                        .orElseThrow(
                                ()->new RuntimeException(
                                        "Usuario no encontrado"
                                )
                        );


        usuario.setEstado(
                EstadoUsuario.activo
        );


        usuarioRepository.save(usuario);

    }


}