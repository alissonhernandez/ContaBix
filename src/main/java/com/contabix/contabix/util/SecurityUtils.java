package com.contabix.contabix.util;

import com.contabix.contabix.model.Usuario;
import jakarta.servlet.http.HttpSession;

public class SecurityUtils {

    // Obtener el usuario logueado desde la sesión
    public static Usuario getUsuario(HttpSession session) {
        if (session == null) return null;
        Object obj = session.getAttribute("usuario");
        if (obj instanceof Usuario) {
            return (Usuario) obj;
        }
        return null;
    }

    // Verificar si el usuario tiene alguno de los roles permitidos
    // Roles en BD: "admin", "contador", "auditor"
    public static boolean tieneRol(HttpSession session, String... rolesPermitidos) {
        Usuario usuario = getUsuario(session);
        if (usuario == null || usuario.getRol() == null) {
            return false;
        }

        String nombreRol = usuario.getRol().getNombre();
        if (nombreRol == null) return false;

        nombreRol = nombreRol.toLowerCase();

        for (String rol : rolesPermitidos) {
            if (nombreRol.equals(rol.toLowerCase())) {
                return true;
            }
        }

        return false;
    }
}
