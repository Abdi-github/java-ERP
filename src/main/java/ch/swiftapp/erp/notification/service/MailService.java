package ch.swiftapp.erp.notification.service;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

import java.util.Locale;
import java.util.Map;

/**
 * Low-level mail sending service.
 *
 * <p>Wraps {@link JavaMailSender} and the Thymeleaf {@link TemplateEngine} to
 * render HTML email templates and dispatch them via SMTP.</p>
 *
 * <p>This service is intentionally thin — it has no knowledge of business events.
 * Higher-level services ({@link NotificationService}, {@link MailCampaignService})
 * orchestrate what to send and to whom.</p>
 *
 * <h2>Template convention</h2>
 * Email templates live under {@code classpath:/templates/email/} and are resolved
 * as standard Thymeleaf templates (e.g. template name {@code "email/sales-order-confirmed"}
 * maps to {@code templates/email/sales-order-confirmed.html}).
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class MailService {

    private final JavaMailSender mailSender;
    private final TemplateEngine templateEngine;

    @Value("${app.mail.from:noreply@swiftapp.ch}")
    private String fromAddress;

    @Value("${app.mail.from-name:SwiftApp ERP}")
    private String fromName;

    /**
     * Send an HTML email rendered from a Thymeleaf template.
     *
     * @param to           recipient email address
     * @param subject      email subject
     * @param templateName Thymeleaf template path (e.g. {@code "email/sales-order-confirmed"})
     * @param variables    template variables injected into the Thymeleaf context
     * @param locale       locale used for template rendering (de/fr/it/en)
     * @throws MessagingException if SMTP delivery fails
     */
    public void sendHtml(String to, String subject, String templateName,
                         Map<String, Object> variables, Locale locale) throws MessagingException {
        Context ctx = new Context(locale);
        ctx.setVariables(variables);
        String htmlBody = templateEngine.process(templateName, ctx);
        doSend(to, subject, htmlBody);
    }

    /**
     * Send an HTML email with a pre-rendered body string.
     *
     * <p>Use this when the body is already rendered (e.g. stored in a campaign template).</p>
     *
     * @param to      recipient email address
     * @param subject email subject
     * @param html    fully rendered HTML body
     * @throws MessagingException if SMTP delivery fails
     */
    public void sendRaw(String to, String subject, String html) throws MessagingException {
        doSend(to, subject, html);
    }

    // ── Private ─────────────────────────────────────────────

    private void doSend(String to, String subject, String htmlBody) throws MessagingException {
        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");
            helper.setFrom(fromAddress, fromName);
            helper.setTo(to);
            helper.setSubject(subject);
            helper.setText(htmlBody, true);
            mailSender.send(message);
            log.info("[MAIL] Sent '{}' to '{}'", subject, to);
        } catch (java.io.UnsupportedEncodingException e) {
            throw new MessagingException("Unsupported encoding for from-name: " + fromName, e);
        }
    }
}

