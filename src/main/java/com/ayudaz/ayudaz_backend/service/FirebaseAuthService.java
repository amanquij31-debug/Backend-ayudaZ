package com.ayudaz.ayudaz_backend.service;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseToken;
import org.springframework.stereotype.Service;

@Service
public class FirebaseAuthService {

    /**
     * Verifica el token ID de Firebase y devuelve el token decodificado.
     * @param idToken token JWT de Firebase (obtenido del frontend)
     * @return FirebaseToken con la información del usuario (UID, email, nombre, etc.)
     * @throws Exception si el token es inválido o ha expirado
     */
    public FirebaseToken verifyToken(String idToken) throws Exception {
        return FirebaseAuth.getInstance().verifyIdToken(idToken);
    }
}