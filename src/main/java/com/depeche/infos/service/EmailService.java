package com.depeche.infos.service;

import com.depeche.infos.entity.AbonneNewsletter;
import com.depeche.infos.entity.Article;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import org.springframework.web.util.HtmlUtils;

import java.io.UnsupportedEncodingException;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Locale;

// Construit et envoie UN e-mail de newsletter à UN abonné.
// Le serveur d'envoi (Mailpit en test, Brevo en production) est choisi dans application.properties (spring.mail.*).
@Service
public class EmailService {

    private static final DateTimeFormatter FORMAT_DATE = DateTimeFormatter.ofPattern("d MMMM yyyy", Locale.FRENCH);

    // Couleurs du site (tailwind.config.js du frontend)
    private static final String MARINE = "#1B2A4A";
    private static final String BRIQUE = "#C30909"; // rouge du logo
    private static final String CREME = "#F6F1E7";

    private final JavaMailSender mailSender;

    @Value("${app.mail.expediteur:}")
    private String expediteur;

    @Value("${app.mail.nom-expediteur:Dépêche 226}")
    private String nomExpediteur;

    // Adresse du site Angular : pour les liens "Lire la suite", "Se désabonner" et les images
    @Value("${app.site.url:http://localhost:4200}")
    private String urlSite;

    public EmailService(JavaMailSender mailSender) {
        this.mailSender = mailSender;
    }

    // Envoie la newsletter ; lève une exception si l'envoi échoue (le service appelant compte les échecs)
    public void envoyerNewsletter(AbonneNewsletter abonne, String objet, List<Article> articles)
            throws MessagingException, UnsupportedEncodingException {
        String lienDesabonnement = urlSite + "/newsletter/desabonnement?jeton=" + abonne.getJeton();

        MimeMessage message = mailSender.createMimeMessage();
        // true = message "multipart" : une version texte ET une version HTML
        MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");
        helper.setFrom(expediteur, nomExpediteur);
        helper.setTo(abonne.getEmail()); // UN destinataire : les adresses des autres ne sont jamais visibles
        helper.setSubject(objet);
        helper.setText(construireTexte(articles, lienDesabonnement), construireHtml(objet, articles, lienDesabonnement));

        // En-tête standard : Gmail / Outlook affichent un bouton "Se désabonner" à côté de l'expéditeur
        message.addHeader("List-Unsubscribe", "<" + lienDesabonnement + ">");

        mailSender.send(message);
    }

    // "Mot de passe oublié" : e-mail avec le lien de réinitialisation (valable 30 minutes)
    public void envoyerReinitialisation(String email, String prenom, String jeton)
            throws MessagingException, UnsupportedEncodingException {
        String lien = urlSite + "/reinitialiser-mot-de-passe?jeton=" + jeton;

        MimeMessage message = mailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");
        helper.setFrom(expediteur, nomExpediteur);
        helper.setTo(email);
        helper.setSubject("Réinitialisation de votre mot de passe");

        String texte = "Bonjour " + prenom + ",\n\n"
                + "Vous avez demandé à changer votre mot de passe. Ouvrez ce lien (valable 30 minutes) :\n"
                + lien + "\n\n"
                + "Si vous n'êtes pas à l'origine de cette demande, ignorez cet e-mail : votre mot de passe ne change pas.\n";

        String html = "<!DOCTYPE html><html lang=\"fr\"><head><meta charset=\"UTF-8\"></head>"
                + "<body style=\"margin:0;padding:24px 12px;background:" + CREME + ";font-family:Arial,Helvetica,sans-serif;color:" + MARINE + ";\">"
                + "<table role=\"presentation\" width=\"100%\" cellpadding=\"0\" cellspacing=\"0\"><tr><td align=\"center\">"
                + "<table role=\"presentation\" width=\"100%\" cellpadding=\"0\" cellspacing=\"0\" style=\"max-width:520px;background:#ffffff;border-top:6px solid " + BRIQUE + ";\">"
                + "<tr><td style=\"padding:28px;\">"
                + "<div style=\"font-family:Georgia,serif;font-size:22px;font-weight:bold;\">DÉPÊCHE 226</div>"
                + "<p style=\"margin:20px 0 0;font-size:15px;line-height:1.5;\">Bonjour " + echapper(prenom) + ",</p>"
                + "<p style=\"margin:10px 0 0;font-size:15px;line-height:1.5;\">Vous avez demandé à changer votre mot de passe. "
                + "Cliquez sur le bouton ci-dessous pour en choisir un nouveau. Ce lien est valable <strong>30 minutes</strong>.</p>"
                + "<a href=\"" + lien + "\" style=\"display:inline-block;margin-top:20px;padding:12px 22px;background:" + BRIQUE
                + ";color:#ffffff;text-decoration:none;font-size:14px;font-weight:bold;border-radius:4px;\">Choisir un nouveau mot de passe</a>"
                + "<p style=\"margin:24px 0 0;font-size:13px;line-height:1.5;color:#7a8396;\">Si vous n'êtes pas à l'origine de cette demande, "
                + "ignorez cet e-mail : votre mot de passe ne change pas.</p>"
                + "</td></tr></table></td></tr></table></body></html>";

        helper.setText(texte, html);
        mailSender.send(message);
    }

    // ===== Version HTML (styles "en ligne" : les messageries ignorent les feuilles de style) =====
    private String construireHtml(String objet, List<Article> articles, String lienDesabonnement) {
        StringBuilder html = new StringBuilder();
        html.append("<!DOCTYPE html><html lang=\"fr\"><head><meta charset=\"UTF-8\">")
            .append("<meta name=\"viewport\" content=\"width=device-width, initial-scale=1\">")
            .append("<title>").append(echapper(objet)).append("</title></head>")
            .append("<body style=\"margin:0;padding:0;background:").append(CREME).append(";font-family:Arial,Helvetica,sans-serif;color:").append(MARINE).append(";\">")
            .append("<table role=\"presentation\" width=\"100%\" cellpadding=\"0\" cellspacing=\"0\" style=\"background:").append(CREME).append(";\"><tr><td align=\"center\" style=\"padding:24px 12px;\">")
            // Bloc central de 600 px max (lisible sur téléphone comme sur ordinateur)
            .append("<table role=\"presentation\" width=\"100%\" cellpadding=\"0\" cellspacing=\"0\" style=\"max-width:600px;background:#ffffff;border-top:6px solid ").append(BRIQUE).append(";\">")
            // En-tête
            .append("<tr><td style=\"padding:24px 28px;background:").append(MARINE).append(";color:#ffffff;\">")
            .append("<div style=\"font-family:Georgia,serif;font-size:26px;font-weight:bold;letter-spacing:1px;\">DÉPÊCHE 226</div>")
            .append("<div style=\"margin-top:6px;font-size:14px;color:#cfd6e4;\">").append(echapper(objet)).append("</div>")
            .append("</td></tr>");

        // Un bloc par article
        for (Article article : articles) {
            String lienArticle = urlSite + "/articles/" + lienPublic(article);
            String extrait = article.getResume() != null && !article.getResume().isBlank()
                    ? article.getResume()
                    : couper(article.getContenu(), 220);

            html.append("<tr><td style=\"padding:24px 28px;border-bottom:1px solid #ECE6D8;\">");
            if (article.getImage() != null && !article.getImage().isBlank()) {
                html.append("<a href=\"").append(lienArticle).append("\"><img src=\"").append(adresseImage(article.getImage()))
                    .append("\" alt=\"\" width=\"544\" style=\"display:block;width:100%;max-width:544px;height:auto;border:0;border-radius:6px;\"></a>");
            }
            html.append("<div style=\"margin-top:12px;font-size:11px;font-weight:bold;text-transform:uppercase;letter-spacing:1px;color:").append(BRIQUE).append(";\">")
                .append(echapper(article.getCategorie() != null ? article.getCategorie().getNom() : ""));
            if (article.getDatePublication() != null) {
                html.append(" · ").append(article.getDatePublication().format(FORMAT_DATE));
            }
            html.append("</div>")
                .append("<h2 style=\"margin:6px 0 0;font-family:Georgia,serif;font-size:21px;line-height:1.3;\">")
                .append("<a href=\"").append(lienArticle).append("\" style=\"color:").append(MARINE).append(";text-decoration:none;\">")
                .append(echapper(article.getTitre())).append("</a></h2>")
                .append("<p style=\"margin:8px 0 0;font-size:15px;line-height:1.5;color:#3b4660;\">").append(echapper(extrait)).append("</p>")
                .append("<a href=\"").append(lienArticle).append("\" style=\"display:inline-block;margin-top:14px;padding:10px 18px;background:").append(BRIQUE)
                .append(";color:#ffffff;text-decoration:none;font-size:13px;font-weight:bold;text-transform:uppercase;border-radius:4px;\">Lire la suite →</a>");
            if (article.getLienVideo() != null) {
                html.append("&nbsp;&nbsp;<a href=\"").append(echapper(article.getLienVideo()))
                    .append("\" style=\"font-size:13px;font-weight:bold;color:").append(MARINE).append(";\">▶ Voir la vidéo</a>");
            }
            html.append("</td></tr>");
        }

        // Pied de l'e-mail : lien de désabonnement (obligatoire)
        html.append("<tr><td style=\"padding:20px 28px;font-size:12px;line-height:1.5;color:#7a8396;text-align:center;\">")
            .append("Vous recevez cet e-mail car vous êtes abonné(e) à la newsletter de Dépêche 226.<br>")
            .append("<a href=\"").append(lienDesabonnement).append("\" style=\"color:").append(BRIQUE).append(";\">Se désabonner</a>")
            .append("</td></tr></table></td></tr></table></body></html>");
        return html.toString();
    }

    // ===== Version texte (messageries qui n'affichent pas le HTML) =====
    private String construireTexte(List<Article> articles, String lienDesabonnement) {
        StringBuilder texte = new StringBuilder("DÉPÊCHE 226 — la sélection de la rédaction\n\n");
        for (Article article : articles) {
            texte.append("• ").append(article.getTitre()).append("\n")
                 .append("  Lire la suite : ").append(urlSite).append("/articles/").append(lienPublic(article)).append("\n");
            if (article.getLienVideo() != null) {
                texte.append("  Vidéo : ").append(article.getLienVideo()).append("\n");
            }
            texte.append("\n");
        }
        texte.append("Se désabonner : ").append(lienDesabonnement).append("\n");
        return texte.toString();
    }

    // Adresse publique de l'article : son slug lisible (le numéro seulement si l'article n'en a pas encore)
    private static String lienPublic(Article article) {
        return article.getSlug() != null ? article.getSlug() : String.valueOf(article.getId());
    }

    // "/uploads/photo.jpg" → "http://localhost:4200/uploads/photo.jpg" (une messagerie a besoin d'une adresse complète)
    private String adresseImage(String image) {
        return image.startsWith("http") ? image : urlSite + image;
    }

    // Les titres et résumés sont écrits par l'équipe : on neutralise < > & " pour ne jamais casser l'e-mail
    private static String echapper(String texte) {
        return texte == null ? "" : HtmlUtils.htmlEscape(texte, "UTF-8");
    }

    private static String couper(String texte, int longueur) {
        if (texte == null) {
            return "";
        }
        return texte.length() <= longueur ? texte : texte.substring(0, longueur).trim() + "…";
    }
}
