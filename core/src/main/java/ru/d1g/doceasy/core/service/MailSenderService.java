package ru.d1g.doceasy.core.service;

import freemarker.template.Configuration;
import freemarker.template.Template;
import freemarker.template.TemplateException;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import org.springframework.ui.freemarker.FreeMarkerTemplateUtils;

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;
import java.io.IOException;
import java.util.Map;

@Service
public class MailSenderService {
    private final JavaMailSender javaMailSender;
    private final Configuration emailFreemarkerConfiguration;

    private String mailFrom = "no-reply@nomia.net";

    public MailSenderService(JavaMailSender javaMailSender, Configuration emailFreemarkerConfiguration) {
        this.javaMailSender = javaMailSender;
        this.emailFreemarkerConfiguration = emailFreemarkerConfiguration;
    }

    public void send(SimpleMailMessage mailMessage) {
        if (mailMessage.getFrom() == null || (mailMessage.getFrom() != null && mailMessage.getFrom().isEmpty())) {
            mailMessage.setFrom(mailFrom);
        }
        javaMailSender.send(mailMessage);
    }

    public void send(String to, String subject, String template, Map<String, Object> model) throws MessagingException {
        MimeMessage mimeMessage = javaMailSender.createMimeMessage();
        MimeMessageHelper message = new MimeMessageHelper(mimeMessage, "utf-8");
        message.setTo(to);
        message.setSubject(subject);
        message.setFrom(mailFrom);

        try {
            Template freemarkerTemplate = emailFreemarkerConfiguration.getTemplate(template);
            String text = FreeMarkerTemplateUtils.processTemplateIntoString(freemarkerTemplate, model);
            message.setText(text, true);
            javaMailSender.send(message.getMimeMessage());
        } catch (IOException e) {
            throw new RuntimeException("email send failed - template '" + template + "' not found");
        } catch (TemplateException e) {
            throw new RuntimeException("email send failed", e);
        }
    }

    public void send(SimpleMailMessage mailMessage, String template, Map<String, Object> model) {
        try {
            Template freemarkerTemplate = emailFreemarkerConfiguration.getTemplate(template);
            String text = FreeMarkerTemplateUtils.processTemplateIntoString(freemarkerTemplate, model);
            mailMessage.setText(text);
            send(mailMessage);
        } catch (IOException e) {
            throw new RuntimeException("email send failed - template '" + template + "' not found");
        } catch (TemplateException e) {
            throw new RuntimeException("email send failed", e);
        }
    }
}
