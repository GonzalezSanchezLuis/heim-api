package com.heim.api.notification.application.service.email.templates;

public class AuthEmailTemplate {
    private static final String BRAND_COLOR = "#4F46E5";
    private static final String APP_URL = "https://heimapp.com.co";

    public static String buildPasswordReset(String firstName, String token) {
        String resetLink = APP_URL + "/reset-password?token=" + token;

        return """
                <!DOCTYPE html>
                <html>
                <body style="margin:0; padding:0; background-color:#f4f7f9; font-family: sans-serif;">
                    <table width="100%%" border="0" cellspacing="0" cellpadding="0">
                        <tr>
                            <td align="center" style="padding: 20px;">
                                <table width="600" style="background:#ffffff; border-radius:12px; overflow:hidden; border:1px solid #e5e7eb;">
                                    <tr>
                                        <td style="padding: 40px;">
                                            <h1 style="font-size: 24px; color: #111827;">Hola, %s!</h1>
                                            <p style="color: #4b5563; font-size: 20px;">
                                            Sabemos que olvidar una contraseña pasa — especialmente cuando tienes muchas cosas en mente.
                
                                              Haz clic en el botón de abajo para crear una nueva y continuar organizando tu mudanza sin interrupciones.
                
                                              Por tu seguridad, este enlace es temporal.</p>
                                            <div style="text-align: center; padding: 30px 0;">
                                                <a href="%s" style="background-color: %s; color: white; padding: 14px 28px; text-decoration: none; border-radius: 8px; font-weight: bold;">
                                                    Restablecer contraseña
                                                </a>
                                            </div>
                                            <p style="font-size: 12px; color: #9ca3af; text-align: center;">
                                               Si no solicitaste este cambio, puedes ignorar este correo con tranquilidad.<br>
                                                © 2026 Heim Project. Bogotá D.C.
                                            </p>
                                        </td>
                                    </tr>
                                </table>
                            </td>
                        </tr>
                    </table>
                </body>
                </html>
                """.formatted(firstName, resetLink, BRAND_COLOR);
    }
}
