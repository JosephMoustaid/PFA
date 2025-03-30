package spring.bricole.util;

import spring.bricole.model.*;
import spring.bricole.common.*;
import java.time.LocalDateTime;
import java.util.*;

public class DummyDataGenerator {

    public static List<Object> generateDummyData() {
        List<Object> dummyData = new ArrayList<>();

        // Generate Admin
        dummyData.addAll(generateAdmins());

        // Generate Users (both Employee and Employer)
        List<User> users = generateUsers();
        dummyData.addAll(users);

        // Separate Employees and Employers
        List<Employee> employees = new ArrayList<>();
        List<Employer> employers = new ArrayList<>();
        for (User user : users) {
            if (user instanceof Employee) {
                employees.add((Employee) user);
            } else if (user instanceof Employer) {
                employers.add((Employer) user);
            }
        }

        // Generate Jobs
        List<Job> jobs = generateJobs(employers);
        dummyData.addAll(jobs);

        // Generate Conversations and Messages
        dummyData.addAll(generateConversationsAndMessages(users));

        // Generate Notifications
        dummyData.addAll(generateNotifications(users));

        // Generate Reviews
        dummyData.addAll(generateReviews(employees, users));

        return dummyData;
    }

    private static List<Admin> generateAdmins() {
        return Arrays.asList(
                new Admin("admin1@bricole.ma", "Admin123!"),
                new Admin("admin2@bricole.ma", "Admin456@")
        );
    }

    private static List<User> generateUsers() {
        List<User> users = new ArrayList<>();

        // Moroccan male first names
        String[] maleFirstNames = {"Mohamed", "Ahmed", "Youssef", "Ali", "Omar", "Karim", "Hassan", "Ibrahim", "Abdellah", "Mustapha"};

        // Moroccan female first names
        String[] femaleFirstNames = {"Fatima", "Amina", "Khadija", "Zahra", "Hafsa", "Asma", "Naima", "Samira", "Leila", "Sanaa"};

        // Moroccan last names
        String[] lastNames = {"El Amrani", "Benjelloun", "Alaoui", "Cherkaoui", "Bennani", "Rahmani", "El Fassi", "Idrissi", "Bouzidi", "Tazi"};

        // Moroccan cities and addresses
        String[] cities = {"Casablanca", "Rabat", "Marrakech", "Fes", "Tangier", "Agadir", "Meknes", "Oujda", "Kenitra", "Tetouan"};
        String[] addresses = {
                "123 Avenue Hassan II", "45 Rue Mohammed V", "78 Boulevard Mohamed VI",
                "12 Derb Sidi Bouloukat", "33 Place Jemaa el-Fna", "67 Avenue des FAR",
                "89 Rue de la Liberté", "101 Avenue Palestine", "56 Derb El Arsa", "22 Rue Ibn Batouta"
        };

        // Generate 10 Employees
        for (int i = 0; i < 10; i++) {
            Gender gender = i % 2 == 0 ? Gender.MALE : Gender.FEMALE;
            String firstName = gender == Gender.MALE ?
                    maleFirstNames[i % maleFirstNames.length] :
                    femaleFirstNames[i % femaleFirstNames.length];
            String lastName = lastNames[i % lastNames.length];
            String email = firstName.toLowerCase() + "." + lastName.toLowerCase().replace(" ", "") + (i+1) + "@gmail.com";

            Employee employee = new Employee(
                    firstName,
                    lastName,
                    email,
                    "Password123!",
                    212, // Morocco country code
                    "6" + String.format("%08d", new Random().nextInt(100000000)),
                    addresses[i % addresses.length] + ", " + cities[i % cities.length],
                    gender,
                    "profile_" + (i+1) + ".jpg"
            );

            // Set skills
            Skill[] allSkills = Skill.values();
            int skillCount = 2 + new Random().nextInt(3); // 2-4 skills
            for (int j = 0; j < skillCount; j++) {
                employee.addSkill(allSkills[new Random().nextInt(allSkills.length)]);
            }

            // Set job preferences
            JobCategory[] allCategories = JobCategory.values();
            int prefCount = 1 + new Random().nextInt(2); // 1-2 preferences
            for (int j = 0; j < prefCount; j++) {
                employee.addJobPreference(allCategories[new Random().nextInt(allCategories.length)]);
            }

            // Set availability (randomly modify some days)
            String[] days = {"Monday", "Tuesday", "Wednesday", "Thursday", "Friday", "Saturday", "Sunday"};
            for (int j = 0; j < 2; j++) { // Change 2 random days
                String day = days[new Random().nextInt(days.length)];
                Availability[] availabilities = Availability.values();
                employee.updateAvailability(day, availabilities[new Random().nextInt(availabilities.length)]);
            }

            users.add(employee);
        }

        // Generate 5 Employers
        for (int i = 0; i < 5; i++) {
            Gender gender = i % 2 == 0 ? Gender.MALE : Gender.FEMALE;
            String firstName = gender == Gender.MALE ?
                    maleFirstNames[(i+5) % maleFirstNames.length] :
                    femaleFirstNames[(i+5) % femaleFirstNames.length];
            String lastName = lastNames[(i+5) % lastNames.length];
            String email = "contact@" + lastName.toLowerCase().replace(" ", "") + "company.com";

            Employer employer = new Employer(
                    firstName,
                    lastName,
                    email,
                    "Password123!",
                    212, // Morocco country code
                    "6" + String.format("%08d", new Random().nextInt(100000000)),
                    addresses[(i+5) % addresses.length] + ", " + cities[(i+5) % cities.length],
                    gender,
                    "profile_emp_" + (i+1) + ".jpg"
            );

            users.add(employer);
        }

        return users;
    }

    private static List<Job> generateJobs(List<Employer> employers) {
        List<Job> jobs = new ArrayList<>();

        // Moroccan job titles and descriptions
        String[] titles = {
                "Plombier nécessaire pour réparation urgente",
                "Electricien pour installation maison neuve",
                "Peintre bâtiment expérimenté",
                "Menuisier pour fabrication meubles sur mesure",
                "Jardinier pour entretien espace vert",
                "Technicien de maintenance climatisation",
                "Maçon pour construction mur",
                "Nettoyeur professionnel pour bureaux",
                "Cuisinier pour événement familial",
                "Informaticien pour dépannage ordinateurs"
        };

        String[] descriptions = {
                "Recherche un professionnel qualifié pour réparer une fuite d'eau dans la salle de bain.",
                "Installation électrique complète pour une maison de 150m2 en construction.",
                "Peinture intérieure et extérieure d'une villa à Tamaris. Expérience requise.",
                "Fabrication de meubles en bois massif selon des plans spécifiques.",
                "Entretien régulier d'un jardin de 500m2 avec système d'irrigation.",
                "Dépannage et maintenance de systèmes de climatisation centrale.",
                "Construction d'un mur de clôture en briques de 20m de long.",
                "Nettoyage hebdomadaire de bureaux dans le centre-ville de Casablanca.",
                "Préparation de repas pour un mariage d'environ 100 personnes.",
                "Dépannage et mise à jour de plusieurs ordinateurs dans une petite entreprise."
        };

        for (int i = 0; i < 15; i++) {
            Employer employer = employers.get(i % employers.size());
            JobStatus status = JobStatus.values()[new Random().nextInt(JobStatus.values().length)];

            Job job = new Job(
                    titles[i % titles.length],
                    descriptions[i % descriptions.length],
                    JobCategory.values()[new Random().nextInt(JobCategory.values().length)],
                    status,
                    "Location",
                    (float) (500 + new Random().nextInt(5000)),  // 500-5500 MAD
                    LocalDateTime.now().minusDays(new Random().nextInt(30))
            );
            job.setEmployer(employer);

            employer.postJob(job);
            jobs.add(job);
        }

        return jobs;
    }

    private static List<Conversation> generateConversationsAndMessages(List<User> users) {
        List<Conversation> conversations = new ArrayList<>();

        // Create conversations between random pairs of users
        for (int i = 0; i < 10; i++) {
            User user1 = users.get(new Random().nextInt(users.size()));
            User user2;
            do {
                user2 = users.get(new Random().nextInt(users.size()));
            } while (user2.equals(user1));

            String[] possibleMessages = {
                    "Bonjour, comment allez-vous?",
                    "Salam, je suis intéressé par votre service",
                    "Avez-vous des disponibilités cette semaine?",
                    "Quel est votre tarif pour ce type de travail?",
                    "Pouvez-vous me donner plus de détails sur votre expérience?",
                    "Merci pour votre réponse rapide",
                    "Je suis disponible demain matin, ça vous convient?",
                    "J'ai besoin de ce service d'urgence, pouvez-vous m'aider?",
                    "Avez-vous des références ou avis de précédents clients?",
                    "Je confirme notre rendez-vous pour demain à 10h"
            };

            String lastMessage = possibleMessages[new Random().nextInt(possibleMessages.length)];
            LocalDateTime lastMessageTime = LocalDateTime.now().minusHours(new Random().nextInt(72));

            Conversation conversation = new Conversation(user1, user2, lastMessage, lastMessageTime);

            // Add 2-5 messages to each conversation
            int messageCount = 2 + new Random().nextInt(4);
            for (int j = 0; j < messageCount; j++) {
                boolean isSent = j % 2 == 0;
                User sender = isSent ? user1 : user2;
                String content = possibleMessages[new Random().nextInt(possibleMessages.length)];
                LocalDateTime sentAt = lastMessageTime.minusMinutes(new Random().nextInt(120));

                Message message = new Message(content, sentAt, j == messageCount-1, isSent, sender);
                conversation.addMessage(message);
            }

            conversations.add(conversation);
        }

        return conversations;
    }

    private static List<Notification> generateNotifications(List<User> users) {
        List<Notification> notifications = new ArrayList<>();

        String[] notificationMessages = {
                "Votre demande a été acceptée",
                "Nouvelle offre d'emploi correspondant à vos compétences",
                "Rappel: Vous avez un rendez-vous demain",
                "Vous avez reçu un nouveau message",
                "Votre candidature a été examinée",
                "Nouvel avis sur votre profil",
                "Mise à jour de statut pour votre offre d'emploi",
                "Paiement reçu pour votre service",
                "Confirmation de réservation",
                "Votre profil a été mis à jour avec succès"
        };

        for (int i = 0; i < 20; i++) {
            User sender = users.get(new Random().nextInt(users.size()));
            User receiver;
            do {
                receiver = users.get(new Random().nextInt(users.size()));
            } while (receiver.equals(sender));

            String message = notificationMessages[new Random().nextInt(notificationMessages.length)];
            LocalDateTime createdAt = LocalDateTime.now().minusHours(new Random().nextInt(72));

            notifications.add(new Notification(message, receiver, createdAt));
        }

        return notifications;
    }

    private static List<Review> generateReviews(List<Employee> employees, List<User> users) {
        List<Review> reviews = new ArrayList<>();

        String[] reviewContents = {
                "Travail excellent, très professionnel!",
                "Le travail a été fait dans les temps et avec qualité",
                "Je recommande ce professionnel, très satisfait",
                "Bon travail mais un peu en retard sur le planning",
                "Résultat moyen, pourrait faire mieux",
                "Service exceptionnel, je ferai appel à nouveau",
                "Communication difficile mais travail correct",
                "Très bon rapport qualité-prix",
                "Déçu par la qualité du travail fourni",
                "Professionnel sérieux et compétent"
        };

        String[] reviewerNames = {"Ahmed B.", "Fatima Z.", "Karim L.", "Samira M.", "Youssef K.", "Leila A.", "Omar S.", "Naima T.", "Hassan R.", "Amina C."};

        for (int i = 0; i < 15; i++) {
            Employee emp = employees.get(new Random().nextInt(employees.size()));
            User reviewer = users.get(new Random().nextInt(users.size()));

            Review review = new Review(
                    reviewerNames[new Random().nextInt(reviewerNames.length)],
                    reviewContents[new Random().nextInt(reviewContents.length)],
                    1 + new Random().nextInt(5) // 1-5 stars
            );

            review.setReviewedEmployee(emp);
            review.setReviewerId(reviewer.getId());
            reviews.add(review);
        }

        return reviews;
    }
}