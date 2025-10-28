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

    private static final String PERSONAL_SEMESTER = "PERSONAL";
    private static final String DEFAULT_SESSION = "2024-Fall";

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
        User emma = ensureUser("student1@school.test", "Emma Laurent", RoleType.STUDENT);
        User lucas = ensureUser("student2@school.test", "Lucas Bernard", RoleType.STUDENT);
        User professor = ensureUser("professor@school.test", "Sophie Martin", RoleType.PROFESSOR);
        ensureUser("admin@school.test", "Noah Leroy", RoleType.ADMIN);

        seedGrades(emma, lucas);
        seedTimetables(emma, lucas, professor);
        List<StudentRequest> requests = seedRequests(emma, lucas);
        seedMessaging(emma, lucas, professor);
        seedPayments(emma, lucas, requests);
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

    private void seedGrades(User emma, User lucas) {
        if (gradeRepository.count() > 0) {
            return;
        }
        gradeRepository.saveAll(List.of(
                Grade.builder().student(emma).moduleCode("ALG204").moduleTitle("Algorithms II").session(DEFAULT_SESSION).grade(15.5).publishedAt(Instant.now().minusSeconds(720000)).build(),
                Grade.builder().student(emma).moduleCode("NET210").moduleTitle("Computer Networks").session(DEFAULT_SESSION).grade(13.0).publishedAt(Instant.now().minusSeconds(604800)).build(),
                Grade.builder().student(emma).moduleCode("UX150").moduleTitle("Human Computer Interaction").session(DEFAULT_SESSION).grade(16.5).publishedAt(Instant.now().minusSeconds(432000)).build(),
                Grade.builder().student(lucas).moduleCode("AI220").moduleTitle("Applied Machine Learning").session(DEFAULT_SESSION).grade(14.2).publishedAt(Instant.now().minusSeconds(518400)).build(),
                Grade.builder().student(lucas).moduleCode("DB230").moduleTitle("Advanced Databases").session(DEFAULT_SESSION).grade(17.0).publishedAt(Instant.now().minusSeconds(259200)).build()
        ));
    }

    private void seedTimetables(User emma, User lucas, User professor) {
        LocalDate currentMonday = LocalDate.now().with(TemporalAdjusters.previousOrSame(DayOfWeek.MONDAY));
        LocalDate nextMonday = currentMonday.plusWeeks(1);

        String emmaCurrent = """
{
  "events": [
    { "day": "Monday", "start": "08:30", "end": "10:00", "title": "Data Mining", "type": "Lecture", "room": "D-201", "teacher": "Dr. Allen" },
    { "day": "Monday", "start": "10:15", "end": "12:15", "title": "Python for Analytics", "type": "Lab", "room": "Lab 4", "teacher": "Ms. Clark" },
    { "day": "Tuesday", "start": "09:00", "end": "11:00", "title": "Statistics Clinic", "type": "Workshop", "room": "B-110", "teacher": "Prof. Nguyen" },
    { "day": "Wednesday", "start": "13:30", "end": "15:30", "title": "Machine Learning Project", "type": "Lab", "room": "AI Studio", "teacher": "Coach Reed" },
    { "day": "Thursday", "start": "08:30", "end": "10:00", "title": "Ethics in AI", "type": "Seminar", "room": "Auditorium", "teacher": "Dr. Patel" },
    { "day": "Thursday", "start": "10:15", "end": "11:45", "title": "Data Visualization Studio", "type": "Lab", "room": "Design Hub", "teacher": "Prof. Martin" },
    { "day": "Friday", "start": "09:30", "end": "11:00", "title": "Startup Lab", "type": "Workshop", "room": "Innovation Lab", "teacher": "Mentor Squad" }
  ]
}
""";

        String emmaNext = """
{
  "events": [
    { "day": "Monday", "start": "09:00", "end": "11:00", "title": "Deep Learning", "type": "Lecture", "room": "D-204", "teacher": "Dr. Allen" },
    { "day": "Tuesday", "start": "08:30", "end": "10:30", "title": "TensorFlow Practice", "type": "Lab", "room": "Lab 4", "teacher": "Ms. Clark" },
    { "day": "Tuesday", "start": "14:00", "end": "15:30", "title": "Career Coaching", "type": "Seminar", "room": "Advising Center", "teacher": "Career Team" },
    { "day": "Wednesday", "start": "10:00", "end": "12:00", "title": "Data Ethics Workshop", "type": "Workshop", "room": "B-110", "teacher": "Prof. Nguyen" },
    { "day": "Thursday", "start": "09:30", "end": "11:30", "title": "Capstone Sprint", "type": "Lab", "room": "AI Studio", "teacher": "Coach Reed" },
    { "day": "Friday", "start": "08:30", "end": "10:00", "title": "Visualization Critique", "type": "Seminar", "room": "Design Hub", "teacher": "Prof. Martin" },
    { "day": "Friday", "start": "10:15", "end": "11:45", "title": "Innovation Pitch", "type": "Workshop", "room": "Innovation Lab", "teacher": "Mentor Squad" }
  ]
}
""";

        String lucasCurrent = """
{
  "events": [
    { "day": "Monday", "start": "10:00", "end": "12:00", "title": "Network Forensics", "type": "Lecture", "room": "C-305", "teacher": "Prof. Martin" },
    { "day": "Monday", "start": "13:30", "end": "15:30", "title": "Security Operations Lab", "type": "Lab", "room": "Cyber Lab", "teacher": "Ms. Kim" },
    { "day": "Tuesday", "start": "09:00", "end": "10:30", "title": "Secure Coding", "type": "Workshop", "room": "C-201", "teacher": "Mr. Ruiz" },
    { "day": "Wednesday", "start": "08:30", "end": "10:00", "title": "Incident Response", "type": "Lecture", "room": "C-305", "teacher": "Dr. Martin" },
    { "day": "Wednesday", "start": "10:15", "end": "12:15", "title": "Blue Team Simulation", "type": "Lab", "room": "Cyber Range", "teacher": "Ms. Kim" },
    { "day": "Thursday", "start": "14:00", "end": "16:00", "title": "Penetration Testing", "type": "Lab", "room": "Cyber Lab", "teacher": "Coach Rivera" },
    { "day": "Friday", "start": "09:30", "end": "11:00", "title": "Security Briefing", "type": "Seminar", "room": "War Room", "teacher": "Guest Team" }
  ]
}
""";

        String lucasNext = """
{
  "events": [
    { "day": "Monday", "start": "08:30", "end": "10:30", "title": "Cloud Security", "type": "Lecture", "room": "C-305", "teacher": "Dr. Martin" },
    { "day": "Monday", "start": "11:00", "end": "13:00", "title": "Red Team Lab", "type": "Lab", "room": "Cyber Range", "teacher": "Ms. Kim" },
    { "day": "Tuesday", "start": "10:00", "end": "12:00", "title": "Threat Hunting", "type": "Workshop", "room": "C-201", "teacher": "Mr. Ruiz" },
    { "day": "Wednesday", "start": "09:00", "end": "11:00", "title": "Digital Forensics Studio", "type": "Lab", "room": "Cyber Lab", "teacher": "Coach Rivera" },
    { "day": "Thursday", "start": "08:30", "end": "10:00", "title": "Security Strategy", "type": "Seminar", "room": "War Room", "teacher": "Guest Panel" },
    { "day": "Thursday", "start": "10:15", "end": "12:15", "title": "Pen Test Operations", "type": "Lab", "room": "Cyber Range", "teacher": "Ms. Kim" },
    { "day": "Friday", "start": "13:00", "end": "15:00", "title": "Security Leadership", "type": "Workshop", "room": "Board Room", "teacher": "Security Office" }
  ]
}
""";

        String professorCurrent = """
{
  "events": [
    { "day": "Monday", "start": "10:00", "end": "12:00", "title": "Network Forensics", "type": "Lecture", "room": "C-305", "teacher": "Prof. Martin" },
    { "day": "Thursday", "start": "10:15", "end": "11:45", "title": "Data Visualization Studio", "type": "Lab", "room": "Design Hub", "teacher": "Prof. Martin" }
  ]
}
""";

        String professorNext = """
{
  "events": [
    { "day": "Monday", "start": "10:00", "end": "12:00", "title": "Network Forensics", "type": "Lecture", "room": "C-305", "teacher": "Prof. Martin" },
    { "day": "Thursday", "start": "10:15", "end": "11:45", "title": "Data Visualization Studio", "type": "Lab", "room": "Design Hub", "teacher": "Prof. Martin" }
  ]
}
""";

        saveTimetable(emma.getEmail(), currentMonday, emmaCurrent);
        saveTimetable(emma.getEmail(), nextMonday, emmaNext);
        saveTimetable(lucas.getEmail(), currentMonday, lucasCurrent);
        saveTimetable(lucas.getEmail(), nextMonday, lucasNext);
        saveTimetable(professor.getEmail(), currentMonday, professorCurrent);
        saveTimetable(professor.getEmail(), nextMonday, professorNext);
    }

    private void saveTimetable(String program, LocalDate weekStart, String dataJson) {
        Timetable timetable = timetableRepository
                .findByProgramAndSemesterAndWeekStart(program, PERSONAL_SEMESTER, weekStart)
                .orElseGet(() -> Timetable.builder()
                        .program(program)
                        .semester(PERSONAL_SEMESTER)
                        .weekStart(weekStart)
                        .build());
        timetable.setDataJson(dataJson);
        timetableRepository.save(timetable);
    }

    private List<StudentRequest> seedRequests(User firstStudent, User secondStudent) {
        if (requestRepository.count() > 0) {
            return requestRepository.findAll();
        }
        StudentRequest firstReady = StudentRequest.builder()
                .student(firstStudent)
                .type(RequestType.CERTIFICAT_SCOLARITE)
                .status(RequestStatus.READY)
                .payloadJson("{\"reason\":\"Stage\"}")
                .files(new ArrayList<>())
                .build();
        firstReady.getFiles().add(RequestFile.builder()
                .request(firstReady)
                .filename("piece_identite.pdf")
                .mime("application/pdf")
                .url("/uploads/piece_identite.pdf")
                .build());

        StudentRequest firstInReview = StudentRequest.builder()
                .student(firstStudent)
                .type(RequestType.ATTESTATION)
                .status(RequestStatus.IN_REVIEW)
                .payloadJson("{\"details\":\"Bourse\"}")
                .files(new ArrayList<>())
                .build();

        StudentRequest secondReady = StudentRequest.builder()
                .student(secondStudent)
                .type(RequestType.CERTIFICAT_SCOLARITE)
                .status(RequestStatus.READY)
                .payloadJson("{\"reason\":\"Stage ete\"}")
                .files(new ArrayList<>())
                .build();

        StudentRequest secondDelivered = StudentRequest.builder()
                .student(secondStudent)
                .type(RequestType.ATTESTATION)
                .status(RequestStatus.DELIVERED)
                .payloadJson("{\"details\":\"Transport\"}")
                .files(new ArrayList<>())
                .build();

        requestRepository.saveAll(List.of(firstReady, firstInReview, secondReady, secondDelivered));
        return List.of(firstReady, firstInReview, secondReady, secondDelivered);
    }

    private void seedMessaging(User firstStudent, User secondStudent, User professorUser) {
        if (threadRepository.count() > 0) {
            return;
        }
        ThreadEntity onboardingThread = threadRepository.save(ThreadEntity.builder()
                .subject("Bienvenue sur le campus")
                .createdBy(firstStudent)
                .build());
        ThreadEntity internshipThread = threadRepository.save(ThreadEntity.builder()
                .subject("Questions stage ete")
                .createdBy(firstStudent)
                .build());
        ThreadEntity housingThread = threadRepository.save(ThreadEntity.builder()
                .subject("Logement etudiant")
                .createdBy(secondStudent)
                .build());

        messageRepository.saveAll(List.of(
                MessageEntity.builder().thread(onboardingThread).sender(firstStudent).content("Bonjour, pouvez-vous partager les ressources utiles pour commencer le semestre ?").build(),
                MessageEntity.builder().thread(onboardingThread).sender(professorUser).content("Bonjour Emma, voici le guide d'accueil ainsi que les contacts utiles.").build(),
                MessageEntity.builder().thread(internshipThread).sender(firstStudent).content("Je cherche un stage en data, avez-vous des pistes recentes ?").build(),
                MessageEntity.builder().thread(internshipThread).sender(professorUser).content("Oui, deux entreprises partenaires recrutent actuellement.").build(),
                MessageEntity.builder().thread(housingThread).sender(secondStudent).content("Bonjour, j'ai besoin d'une attestation pour mon logement.").build(),
                MessageEntity.builder().thread(housingThread).sender(professorUser).content("Bonjour Lucas, l'attestation sera disponible demain matin.").build()
        ));
    }

    private void seedPayments(User firstStudent, User secondStudent, List<StudentRequest> requests) {
        if (paymentRepository.count() > 0) {
            return;
        }
        StudentRequest firstRequest = requests.stream()
                .filter(r -> r.getStudent().getId().equals(firstStudent.getId()))
                .findFirst()
                .orElse(null);
        StudentRequest secondRequest = requests.stream()
                .filter(r -> r.getStudent().getId().equals(secondStudent.getId()))
                .findFirst()
                .orElse(null);

        paymentRepository.saveAll(List.of(
                Payment.builder()
                        .student(firstStudent)
                        .request(firstRequest)
                        .amountCents(2_500L)
                        .currency("EUR")
                        .label("Frais de certificat")
                        .paymentMethod("Carte bancaire")
                        .status(PaymentStatus.SUCCEEDED)
                        .justificationUrl("/uploads/paiement_certificat.pdf")
                        .justificationName("paiement_certificat.pdf")
                        .justificationMime("application/pdf")
                        .statusNotes("Reglement confirme manuellement")
                        .build(),
                Payment.builder()
                        .student(firstStudent)
                        .amountCents(8_900L)
                        .currency("EUR")
                        .label("Frais de scolarite")
                        .paymentMethod("Virement")
                        .status(PaymentStatus.PROCESSING)
                        .build(),
                Payment.builder()
                        .student(secondStudent)
                        .request(secondRequest)
                        .amountCents(2_500L)
                        .currency("EUR")
                        .label("Frais dossier logement")
                        .paymentMethod("Carte bancaire")
                        .status(PaymentStatus.SUCCEEDED)
                        .justificationUrl("/uploads/paiement_logement.pdf")
                        .justificationName("paiement_logement.pdf")
                        .justificationMime("application/pdf")
                        .build(),
                Payment.builder()
                        .student(secondStudent)
                        .amountCents(2_500L)
                        .currency("EUR")
                        .label("Assurance etudiant")
                        .paymentMethod("Especes")
                        .status(PaymentStatus.FAILED)
                        .statusNotes("Documents justificatifs manquants")
                        .build()
        ));
    }

    private void seedFaqs() {
        if (faqRepository.count() > 0) {
            return;
        }
        faqRepository.saveAll(List.of(
                new Faq(null, "Comment obtenir un certificat de scolarite ?", "Creez une demande dans le menu E-guichet puis choisissez certificat de scolarite.", List.of("certificat", "administratif")),
                new Faq(null, "Comment payer mes frais ?", "Renseignez le formulaire Paiements avec le justificatif puis attendez la validation du service administratif.", List.of("paiement")),
                new Faq(null, "Comment contacter le support ?", "Utilisez la messagerie integree pour envoyer un message au guichet.", List.of("support"))
        ));
    }
}
