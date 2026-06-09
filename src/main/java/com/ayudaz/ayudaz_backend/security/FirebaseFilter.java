package com.ayudaz.ayudaz_backend.security;

import com.ayudaz.ayudaz_backend.model.Usuario;
import com.ayudaz.ayudaz_backend.service.UsuarioService;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseToken;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
public class FirebaseFilter extends OncePerRequestFilter {

    @Autowired
    private UsuarioService usuarioService;

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain chain) throws ServletException, IOException {

        String authHeader = request.getHeader("Authorization");
        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            try {
                String token = authHeader.substring(7);
                FirebaseToken decodedToken = FirebaseAuth.getInstance().verifyIdToken(token);
                String uid = decodedToken.getUid();
                Usuario usuario = usuarioService.findByFirebaseUid(uid).orElse(null);
                if (usuario != null) {
                    request.setAttribute("usuarioId", usuario.getId());
                    if (usuario.getTipoUsuario() != null) {
                        request.setAttribute("usuarioTipo", usuario.getTipoUsuario().toString());
                    }

                    request.setAttribute("usuarioId", usuario.getId());

                    request.setAttribute(
                            "usuarioTipo",
                            usuario.getTipoUsuario() != null
                                    ? usuario.getTipoUsuario().toString()
                                    : null
                    );
                }
            } catch (Exception e) {
                // Token inválido, no se establecen atributos
                System.out.println("Error verificando token Firebase: " + e.getMessage());
            }
        }
        chain.doFilter(request, response);
    }
}