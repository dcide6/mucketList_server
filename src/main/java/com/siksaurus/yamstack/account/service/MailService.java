package com.siksaurus.yamstack.account.service;

import lombok.RequiredArgsConstructor;
import org.apache.commons.codec.CharEncoding;
import org.springframework.core.io.ClassPathResource;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;
import java.time.LocalDate;

@Service
@RequiredArgsConstructor
public class MailService {

    private final JavaMailSender mailSender;
    private final TemplateEngine templateEngine;

    public String authMailSend(String id, String name) throws MessagingException {
        String code = getAuthCode();
        Context context = new Context();

        context.setVariable("logo", "logo");
        context.setVariable("name", name);
        context.setVariable("code", code);


        String process = templateEngine.process("emails/auth", context);
        MimeMessage mimeMessage = mailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, true, CharEncoding.UTF_8);
        helper.setTo(id);
        helper.setSubject(name+"님의 인증 번호는 "+code+" 입니다.");
        helper.setText(process, true);

        helper.addInline("logo", new ClassPathResource("images/emails/header.png"), "image/png");

        mailSender.send(mimeMessage);
        return code;
    }

    public void welcomeMailSend(String id, String name) throws MessagingException {
        Context context = new Context();
        context.setVariable("logo", "logo");
        context.setVariable("name", name);
        context.setVariable("date", LocalDate.now().toString());

        String process = templateEngine.process("emails/welcome", context);
        MimeMessage mimeMessage = mailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, true, CharEncoding.UTF_8);
        helper.setTo(id);
        helper.setSubject(name+"님, 얌스택 가입을 축하합니다.");
        helper.setText(process, true);

        helper.addInline("logo", new ClassPathResource("images/emails/header.png"), "image/png");

        mailSender.send(mimeMessage);
    }

    private String getAuthCode() {
        char[] charSet = new char[]{'0', '1', '2', '3', '4', '5', '6', '7', '8', '9'};

        String str = "";

        int idx = 0;
        for (int i = 0; i < 6; i++) {
            idx = (int) (charSet.length * Math.random());
            str += charSet[idx];
        }
        return str;
    }
}
