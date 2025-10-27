package com.school.portal.config;

import com.school.portal.common.RoleType;
import com.school.portal.faq.Faq;
import com.school.portal.faq.FaqRepository;
import com.school.portal.grade.Grade;
import com.school.portal.grade.GradeRepository;
import com.school.portal.messaging.MessageEntity;
import com.school.portal.messaging.MessageRepository;
import com.school.portal.messaging.ThreadEntity;
import com.school.portal.messaging.ThreadRepository;
import com.school.portal.payment.Payment;
import com.school.portal.payment.PaymentRepository;
import com.school.portal.payment.PaymentStatus;
import com.school.portal.request.RequestFile;
import com.school.portal.request.RequestStatus;
import com.school.portal.request.RequestType;
import com.school.portal.request.StudentRequest;
import com.school.portal.request.StudentRequestRepository;
import com.school.portal.timetable.Timetable;
import com.school.portal.timetable.TimetableRepository;
import com.school.portal.user.User;
import com.school.portal.user.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.DayOfWeek;
import java.time.Instant;
import java.time.LocalDate;
import java.time.temporal.TemporalAdjusters;
import java.util.ArrayList;
import java.util.List;

@Component
@RequiredArgsConstructor
public class DemoDataSeeder implements ApplicationRunner {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final GradeRepository gradeRepository;
    private final TimetableRepository timetableRepository;
    private final StudentRequestRepository requestRepository;
    private final MessageRepository messageRepository;
    private final ThreadRepository threadRepository;
    private final PaymentRepository paymentRepository;
    private final FaqRepository faqRepository;

    @Override
    @Transactional
    public void run(ApplicationArguments args) {
        User alice = ensureUser("student1@school.test", "Alice Dupont", RoleType.STUDENT);
        User bob = ensureUser("student2@school.test", "Bob Martin", RoleType.STUDENT);
        User staff = ensureUser("staff@school.test", "Claire Support", RoleType.STAFF);
        ensureUser("admin@school.test", "David Admin", RoleType.ADMIN);

        seedGrades(alice, bob);
        seedTimetables();
        var requests = seedRequests(alice, bob);
        seedMessaging(alice, bob, staff);
        seedPayments(alice, bob, requests);
        seedFaqs();
    }

    private User ensureUser(String email, String fullName, RoleType role) {
        return userRepository.findByEmail(email).orElseGet(() -> userRepository.save(User.builder()
                .email(email)
                .passwordHash(passwordEncoder.encode("password"))
                .fullName(fullName)
                .role(role)
                .build()));
    }

    private void seedGrades(User alice, User bob) {
        if (gradeRepository.count() > 0) {
            return;
        }
        gradeRepository.saveAll(List.of(
                Grade.builder().student(alice).moduleCode("MATH101").moduleTitle("Analyse 1").session("2024-S1").grade(14.5).publishedAt(Instant.now().minusSeconds(864000)).build(),
                Grade.builder().student(alice).moduleCode("PHY101").moduleTitle("Physique").session("2024-S1").grade(12.0).publishedAt(Instant.now().minusSeconds(691200)).build(),
                Grade.builder().student(alice).moduleCode("CS102").moduleTitle("Programmation").session("2024-S1").grade(16.5).publishedAt(Instant.now().minusSeconds(432000)).build(),
                Grade.builder().student(bob).moduleCode("CS102").moduleTitle("Programmation").session("2024-S1").grade(13.0).publishedAt(Instant.now().minusSeconds(432000)).build(),
                Grade.builder().student(bob).moduleCode("ENG201").moduleTitle("Communication professionnelle").session("2024-S1").grade(15.0).publishedAt(Instant.now().minusSeconds(172800)).build()
        ));
    }

    private void seedTimetables() {
        if (timetableRepository.count() > 0) {
            return;
        }
        LocalDate currentMonday = LocalDate.now().with(TemporalAdjusters.previousOrSame(DayOfWeek.MONDAY));
        LocalDate nextMonday = currentMonday.plusWeeks(1);
        timetableRepository.saveAll(List.of(
                Timetable.builder()
                        .program("Informatique")
                        .semester("S2")
                        .weekStart(currentMonday)
                        .dataJson("{\"events\":[{\"title\":\"Algèbre\",\"start\":\"09:00\",\"end\":\"11:00\",\"day\":\"Monday\"},{\"title\":\"Projet collaboratif\",\"start\":\"14:00\",\"end\":\"17:00\",\"day\":\"Wednesday\"},{\"title\":\"Examen blanc\",\"start\":\"10:00\",\"end\":\"12:00\",\"day\":\"Friday\"}]}")
                        .build(),
                Timetable.builder()
                        .program("Informatique")
                        .semester("S2")
                        .weekStart(nextMonday)
                        .dataJson("{\"events\":[{\"title\":\"Atelier DevOps\",\"start\":\"09:30\",\"end\":\"11:30\",\"day\":\"Tuesday\"},{\"title\":\"Projet tutoré\",\"start\":\"13:30\",\"end\":\"16:30\",\"day\":\"Thursday\"},{\"title\":\"Sport\",\"start\":\"15:00\",\"end\":\"17:00\",\"day\":\"Friday\"}]}")
                        .build()
        ));
    }

    private List<StudentRequest> seedRequests(User alice, User bob) {
        if (requestRepository.count() > 0) {
            return requestRepository.findAll();
        }
        StudentRequest aliceReady = StudentRequest.builder()
                .student(alice)
                .type(RequestType.CERTIFICAT_SCOLARITE)
                .status(RequestStatus.READY)
                .payloadJson("{\"reason\":\"Stage\"}")
                .files(new ArrayList<>())
                .build();
        aliceReady.getFiles().add(RequestFile.builder()
                .request(aliceReady)
                .filename("piece_identite.pdf")
                .mime("application/pdf")
                .url("/uploads/piece_identite.pdf")
                .build());

        StudentRequest aliceInReview = StudentRequest.builder()
                .student(alice)
                .type(RequestType.ATTESTATION)
                .status(RequestStatus.IN_REVIEW)
                .payloadJson("{\"details\":\"Bourse\"}")
                .files(new ArrayList<>())
                .build();

        StudentRequest bobReady = StudentRequest.builder()
                .student(bob)
                .type(RequestType.CERTIFICAT_SCOLARITE)
                .status(RequestStatus.READY)
                .payloadJson("{\"reason\":\"Stage été\"}")
                .files(new ArrayList<>())
                .build();

        StudentRequest bobDelivered = StudentRequest.builder()
                .student(bob)
                .type(RequestType.ATTESTATION)
                .status(RequestStatus.DELIVERED)
                .payloadJson("{\"details\":\"Transport\"}")
                .files(new ArrayList<>())
                .build();

        requestRepository.saveAll(List.of(aliceReady, aliceInReview, bobReady, bobDelivered));
        return List.of(aliceReady, aliceInReview, bobReady, bobDelivered);
    }

    private void seedMessaging(User alice, User bob, User staff) {
        if (threadRepository.count() > 0) {
            return;
        }
        ThreadEntity certificateThread = threadRepository.save(ThreadEntity.builder()
                .subject("Demande de certificat")
                .createdBy(alice)
                .build());
        ThreadEntity paymentThread = threadRepository.save(ThreadEntity.builder()
                .subject("Suivi paiement frais")
                .createdBy(alice)
                .build());
        ThreadEntity attestationThread = threadRepository.save(ThreadEntity.builder()
                .subject("Attestation transport")
                .createdBy(bob)
                .build());

        messageRepository.saveAll(List.of(
                MessageEntity.builder().thread(certificateThread).sender(alice).content("Bonjour, je souhaite obtenir un certificat de scolarité.").build(),
                MessageEntity.builder().thread(certificateThread).sender(staff).content("Bonjour Alice, nous traitons votre demande.").build(),
                MessageEntity.builder().thread(paymentThread).sender(alice).content("Bonjour, je n'arrive pas à finaliser mon paiement.").build(),
                MessageEntity.builder().thread(paymentThread).sender(staff).content("Bonjour Alice, nous avons relancé le simulateur.").build(),
                MessageEntity.builder().thread(attestationThread).sender(bob).content("Bonjour, j'ai besoin d'une attestation pour mon abonnement.").build(),
                MessageEntity.builder().thread(attestationThread).sender(staff).content("Bonjour Bob, la demande est en cours de validation.").build()
        ));
    }

    private void seedPayments(User alice, User bob, List<StudentRequest> requests) {
        if (paymentRepository.count() > 0) {
            return;
        }
        StudentRequest aliceRequest = requests.stream()
                .filter(r -> r.getStudent().getId().equals(alice.getId()))
                .findFirst()
                .orElse(null);
        StudentRequest bobRequest = requests.stream()
                .filter(r -> r.getStudent().getId().equals(bob.getId()))
                .findFirst()
                .orElse(null);

        paymentRepository.saveAll(List.of(
                Payment.builder().student(alice).request(aliceRequest).amountCents(2500L).currency("EUR").status(PaymentStatus.SUCCEEDED).providerRef("sim-12345").build(),
                Payment.builder().student(alice).amountCents(8900L).currency("EUR").status(PaymentStatus.PENDING).providerRef("sim-44556").build(),
                Payment.builder().student(bob).request(bobRequest).amountCents(2500L).currency("EUR").status(PaymentStatus.SUCCEEDED).providerRef("sim-77889").build(),
                Payment.builder().student(bob).amountCents(2500L).currency("EUR").status(PaymentStatus.FAILED).providerRef("sim-99001").build()
        ));
    }

    private void seedFaqs() {
        if (faqRepository.count() > 0) {
            return;
        }
        faqRepository.saveAll(List.of(
                new Faq(null, "Comment obtenir un certificat de scolarité ?", "Créez une demande dans le menu E-guichet et choisissez certificat de scolarité.", List.of("certificat", "administratif")),
                new Faq(null, "Comment payer mes frais ?", "Utilisez la section Paiements pour créer une intention puis confirmez via le simulateur.", List.of("paiement")),
                new Faq(null, "Comment contacter le support ?", "Utilisez la messagerie intégrée pour envoyer un message au guichet.", List.of("support"))
        ));
    }
}
