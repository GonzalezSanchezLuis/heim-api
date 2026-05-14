package com.heim.api.notification.application.service.email.templates;

public class AccountStatusTemplate {

    public static String build(String name, boolean isActive){
        String title = isActive ? "Tu cuenta está activa" : "Tu cuenta fue pausada";
        String subtitle = isActive ? "Todo está listo" : "No es el final";
        String message = isActive ? "No hicimos nada mágico. Solo quitamos una barrera para que continúes."
                                    :"A veces pausar también es avanzar. Cuando quieras volver, aquí estaremos.";

        String brandColor = isActive ? "#16A34A" : "#F59E0B";
        String background = "#F9FAFB";

        return """
                <!DOCTYPE html>
                            <html>
                            <body style='margin:0;padding:0;background-color:%s;font-family:Segoe UI, sans-serif;'>
                              <table width='100%%' cellpadding='0' cellspacing='0'>
                                <tr>
                                  <td align='center' style='padding:40px'>
                                    <table width='520' style='background:#ffffff;border-radius:20px;padding:40px;border:1px solid #E5E7EB'>
                                      <tr>
                                        <td style='text-align:center'>
                                          <h1 style='font-size:26px;color:#111827;margin-bottom:10px'>%s</h1>
                                          <p style='font-size:18px;color:#4B5563;margin-top:0'>Hola %s</p>
                                        </td>
                                      </tr>
                                      <tr>
                                        <td style='padding:30px 0;text-align:center'>
                                          <p style='font-size:20px;font-weight:600;color:%s'>%s</p>
                                          <p style='font-size:16px;color:#374151;line-height:1.6'>%s</p>
                                        </td>
                                      </tr>
                                      <tr>
                                        <td style='text-align:center;padding-top:20px'>
                                          <p style='font-size:14px;color:#6B7280'>Heim es una herramienta. Tú decides cuándo usarla.</p>
                                        </td>
                                      </tr>
                                      <tr>
                                        <td style='padding-top:30px;text-align:center;color:#9CA3AF;font-size:12px'>
                                          Enviado con intención, no con automatismos.<br/>Equipo Heim
                                        </td>
                                      </tr>
                                    </table>
                                  </td>
                                </tr>
                              </table>
                            </body>
                            </html>
                """.formatted(background,title,name,brandColor,subtitle,message);
    }

}
