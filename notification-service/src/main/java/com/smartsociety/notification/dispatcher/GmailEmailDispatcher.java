package com.smartsociety.notification.dispatcher;

import com.smartsociety.notification.event.ComplaintEventPayload;
import jakarta.mail.internet.MimeMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

@Service
public class GmailEmailDispatcher {

    private static final Logger log = LoggerFactory.getLogger(GmailEmailDispatcher.class);

    private final JavaMailSender mailSender;
    private final String senderEmail;

    public GmailEmailDispatcher(
            JavaMailSender mailSender,
            @Value("${spring.mail.username:}") String senderEmail) {
        this.mailSender = mailSender;
        this.senderEmail = senderEmail;
    }

    public void sendAssignedAlert(ComplaintEventPayload payload) {
        String recipient = "resident" + payload.getResidentId() + "@smartsociety.com";
        String subject = "[Smart Society] Ticket #" + payload.getComplaintId() + " Assigned to Maintenance Staff";
        String htmlContent = buildHtmlTemplate(
                "Complaint Assigned",
                "Your complaint <strong>#" + payload.getComplaintId() + " (" + payload.getTitle() + ")</strong> has been assigned to staff member #" + payload.getAssignedStaffId() + ".",
                payload.getPriority(),
                payload.getStatus(),
                String.valueOf(payload.getSlaDeadline()),
                "#059669"
        );

        sendEmail(recipient, subject, htmlContent);
    }

    public void sendSlaBreachAlert(ComplaintEventPayload payload) {
        String recipient = senderEmail; // Admin escalation recipient
        String subject = "[CRITICAL SLA BREACH] Complaint #" + payload.getComplaintId() + " - " + payload.getTitle();
        String htmlContent = buildHtmlTemplate(
                "SLA BREACH ALERT",
                "ATTENTION: Complaint <strong>#" + payload.getComplaintId() + " (\"" + payload.getTitle() + "\")</strong> has exceeded its SLA resolution window without closure. Immediate intervention required.",
                payload.getPriority(),
                payload.getStatus(),
                String.valueOf(payload.getSlaDeadline()),
                "#DC2626"
        );

        sendEmail(recipient, subject, htmlContent);
    }

    public void sendStatusUpdateAlert(ComplaintEventPayload payload) {
        String recipient = "resident" + payload.getResidentId() + "@smartsociety.com";
        String subject = "[Smart Society] Update on Complaint #" + payload.getComplaintId() + " - " + payload.getStatus();
        String htmlContent = buildHtmlTemplate(
                "Status Updated to " + payload.getStatus(),
                "Your maintenance request <strong>#" + payload.getComplaintId() + " (" + payload.getTitle() + ")</strong> status has been updated to <strong>" + payload.getStatus() + "</strong>.",
                payload.getPriority(),
                payload.getStatus(),
                String.valueOf(payload.getSlaDeadline()),
                "#2563EB"
        );

        sendEmail(recipient, subject, htmlContent);
    }

    private void sendEmail(String toEmail, String subject, String htmlBody) {
        if (!StringUtils.hasText(senderEmail)) {
            log.warn("[Gmail Dispatcher] GMAIL_USERNAME not configured. Skipping live SMTP dispatch.");
            return;
        }

        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");

            helper.setFrom(senderEmail, "Smart Society System");
            helper.setTo(toEmail);
            helper.setSubject(subject);
            helper.setText(htmlBody, true);

            mailSender.send(message);
            log.info("[Gmail Dispatcher] Successfully dispatched notification email to '{}' with subject: '{}'", toEmail, subject);

        } catch (Exception ex) {
            log.error("[Gmail Dispatcher] Failed to dispatch email via Gmail SMTP: {}", ex.getMessage(), ex);
        }
    }

    private String buildHtmlTemplate(String headerTitle, String bodyMessage, String priority, String status, String slaDeadline, String headerColor) {
        return """
            <!DOCTYPE html>
            <html>
            <head>
              <meta charset="utf-8">
              <style>
                body { font-family: -apple-system, BlinkMacSystemFont, 'Segoe UI', Roboto, Helvetica, Arial, sans-serif; background-color: #F8FAFC; margin: 0; padding: 20px; }
                .container { max-width: 580px; margin: 0 auto; background: #FFFFFF; border-radius: 12px; border: 1px solid #E2E8F0; overflow: hidden; box-shadow: 0 4px 6px rgba(0,0,0,0.05); }
                .header { background: %s; padding: 24px; text-align: center; color: #FFFFFF; font-size: 20px; font-weight: bold; }
                .body { padding: 28px; color: #334155; line-height: 1.6; font-size: 14px; }
                .card { background: #F1F5F9; border-radius: 8px; padding: 16px; margin: 18px 0; }
                .card-row { display: flex; justify-content: space-between; padding: 6px 0; font-size: 13px; }
                .badge { font-weight: 700; color: #0F172A; }
                .footer { padding: 20px; text-align: center; font-size: 12px; color: #94A3B8; border-top: 1px solid #E2E8F0; }
              </style>
            </head>
            <body>
              <div class="container">
                <div class="header">%s</div>
                <div class="body">
                  <p>%s</p>
                  <div class="card">
                    <div class="card-row"><span>Priority:</span> <span class="badge">%s</span></div>
                    <div class="card-row"><span>Status:</span> <span class="badge">%s</span></div>
                    <div class="card-row"><span>Target SLA Deadline:</span> <span class="badge">%s</span></div>
                  </div>
                  <p>You can track or manage this ticket live anytime in the Smart Society Resident Portal.</p>
                </div>
                <div class="footer">Smart Society Complaint & Maintenance System • Automated Notification</div>
              </div>
            </body>
            </html>
            """.formatted(headerColor, headerTitle, bodyMessage, priority, status, slaDeadline);
    }
}
