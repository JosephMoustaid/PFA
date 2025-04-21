package spring.bricole.util;



import spring.bricole.model.*;
import spring.bricole.common.*;
import java.time.LocalDateTime;
import java.util.*;

// this class will generate a lot fo data that will be used to create the recommendation system
// it will generate a lot of users, a lot of conversations, a lot of messages, a lot of notifications
// all of the data should be legit and should make sence

public class DummyDataGenerator2 {

    // from here there is new code
    private final FileReaderUtil fileReaderUtil = new FileReaderUtil();


    private final String maleFirstnamesFile = "src/main/resources/static/datageneration/male_first_names.txt";
    private final String lastNamesFile = "src/main/resources/static/datageneration/male_last_names.txt";

    private final String femaleFirstnamesFile = "src/main/resources/static/datageneration/female_first_names.txt";


    private final String jobCategoriesFile = "sec/main/resources/static/datageneration/job_categories.txt";
    private final String jobStatusesFile = "sec/main/resources/static/datageneration/job_statuses.txt";
    private final String rolesFile = "sec/main/resources/static/datageneration/roles.txt";
    private final String applicationStatesFile = "sec/main/resources/static/datageneration/application_states.txt";
    private final String addressesFile = "sec/main/resources/static/datageneration/addresses.txt";



    private List<String> maleFirstNames;
    private List<String> femaleFirstNames ;
    private List<String> lastNames;
    private List<String> jobCategories;
    private List<String> jobStatuses;
    private List<String> roles;
    private List<String> applicationStates;
    private List<String> addresses;


    private Map<String, Availability> availabilityDaysOfWeek1 = new HashMap<>(
            Map.of("Monday", Availability.FULLTIME, "Tuesday", Availability.FULLTIME, "Wednesday", Availability.FULLTIME,
                    "Thursday", Availability.FULLTIME, "Friday", Availability.FULLTIME, "Saturday", Availability.FULLTIME,
                    "Sunday", Availability.FULLTIME)
    );
    private Map<String, Availability> availabilityDaysOfWeek2 = new HashMap<>(
            Map.of("Monday", Availability.PARTTIME, "Tuesday", Availability.FULLTIME, "Wednesday", Availability.PARTTIME,
                    "Thursday", Availability.PARTTIME, "Friday", Availability.FULLTIME, "Saturday", Availability.PARTTIME,
                    "Sunday", Availability.PARTTIME)
    );
    private Map<String, Availability> availabilityDaysOfWeek3 = new HashMap<>(
            Map.of("Monday", Availability.PARTTIME, "Tuesday", Availability.PARTTIME, "Wednesday", Availability.PARTTIME,
                    "Thursday", Availability.PARTTIME, "Friday", Availability.PARTTIME, "Saturday", Availability.FULLTIME,
                    "Sunday", Availability.FULLTIME)
    );
    private Map<String, Availability> availabilityDaysOfWeek4 = new HashMap<>(
            Map.of("Monday", Availability.FULLTIME, "Tuesday", Availability.PARTTIME, "Wednesday", Availability.FULLTIME,
                    "Thursday", Availability.PARTTIME, "Friday", Availability.FULLTIME, "Saturday", Availability.PARTTIME,
                    "Sunday", Availability.FULLTIME)
    );
    private Map<String, Availability> availabilityDaysOfWeek5 = new HashMap<>(
            Map.of("Monday", Availability.PARTTIME, "Tuesday", Availability.PARTTIME, "Wednesday", Availability.PARTTIME,
                    "Thursday", Availability.PARTTIME, "Friday", Availability.FULLTIME, "Saturday", Availability.PARTTIME,
                    "Sunday", Availability.FULLTIME)
    );

    private List<Map<String, Availability>> availabilityDaysOfWeekList = Arrays.asList(availabilityDaysOfWeek1, availabilityDaysOfWeek2, availabilityDaysOfWeek3, availabilityDaysOfWeek4, availabilityDaysOfWeek5);


    // constructor
    public DummyDataGenerator2(){
        maleFirstNames = fileReaderUtil.readFile(maleFirstnamesFile);
        lastNames = fileReaderUtil.readFile(lastNamesFile);
        femaleFirstNames = fileReaderUtil.readFile(femaleFirstnamesFile);
        jobCategories = fileReaderUtil.readFile(jobCategoriesFile);
        jobStatuses = fileReaderUtil.readFile(jobStatusesFile);
        roles = fileReaderUtil.readFile(rolesFile);
        applicationStates = fileReaderUtil.readFile(applicationStatesFile);
        addresses = fileReaderUtil.readFile(addressesFile);
    }

    public List<User> generateUsers() {
        List<Employee> employees = new ArrayList<>();
        List<Employer> employers = new ArrayList<>();
        Random random = new Random();

        // Generate 2200 employees (1100 male + 1100 female)
        for (int i = 0; i < 2200; i++) {
            Employee employee = new Employee();
            String firstName;
            Gender gender;

            if (i < 1100) { // Male employees
                firstName = maleFirstNames.get(random.nextInt(maleFirstNames.size()));
                gender = Gender.MALE;
            } else { // Female employees
                firstName = femaleFirstNames.get(random.nextInt(femaleFirstNames.size()));
                gender = Gender.FEMALE;
            }

            String lastName = lastNames.get(random.nextInt(lastNames.size()));

            employee.setFirstname(firstName);
            employee.setLastname(lastName);
            employee.setEmail(firstName.toLowerCase() + "." + lastName.toLowerCase() +
                    generateRandomString(4) + "@gmail.com");
            employee.setGender(gender);
            employee.setPassword(generateRandomString(8));
            employee.setPhoneNumberPrefix(212);
            employee.setAddress(addresses.get(random.nextInt(addresses.size())));
            employee.setPhoneNumber("0" + generateRandomNumber(7));
            employee.setProfilePicture("");
            employee.setStatus(AccountStatus.ACTIVE);
            employee.setAvailabilityDaysOfWeek(
                    availabilityDaysOfWeekList.get(random.nextInt(availabilityDaysOfWeekList.size())));
            employee.setJobPreferences(getRandomJobCategories(1 + random.nextInt(5)));
            employee.setSkills(getRandomJobSkills(1 + random.nextInt(5)));

            generateReviewsForEmployee(employee, employees, employers);
            employees.add(employee);
        }

        // Generate 1000 employers (500 male + 500 female)
        for (int i = 0; i < 1000; i++) {
            Employer employer = new Employer();
            String firstName;
            Gender gender;

            if (i < 500) { // Male employers
                firstName = maleFirstNames.get(random.nextInt(maleFirstNames.size()));
                gender = Gender.MALE;
            } else { // Female employers
                firstName = femaleFirstNames.get(random.nextInt(femaleFirstNames.size()));
                gender = Gender.FEMALE;
            }

            String lastName = lastNames.get(random.nextInt(lastNames.size()));

            employer.setFirstname(firstName);
            employer.setLastname(lastName);
            employer.setEmail(firstName.toLowerCase() + "." + lastName.toLowerCase() +
                    generateRandomString(4) + "@gmail.com");
            employer.setGender(gender);
            employer.setPassword(generateRandomString(8));
            employer.setPhoneNumberPrefix(212);
            employer.setAddress(addresses.get(random.nextInt(addresses.size())));
            employer.setPhoneNumber("0" + generateRandomNumber(7));
            employer.setProfilePicture("");
            employer.setStatus(AccountStatus.ACTIVE);

            employers.add(employer);
        }

        // Generate jobs for employers
        generateJobs(employers, employees);

        // Combine and return all users
        List<User> users = new ArrayList<>();
        users.addAll(employees);
        users.addAll(employers);
        return users;
    }

    private void generateReviewsForEmployee(Employee employee, List<Employee> employees, List<Employer> employers) {
        Random random = new Random();
        // Generate a random number of reviews for each employee
        int numberOfReviews = 1 + random.nextInt(5); // Each employee will get between 1 to 5 reviews

        for (int j = 0; j < numberOfReviews; j++) {
            // Random reviewer (either an employee or an employer)
            User reviewer = (random.nextBoolean()) ? employees.get(random.nextInt(employees.size())) : employers.get(random.nextInt(employers.size()));

            // Generate a random review content and rating
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
                    "Professionnel sérieux et compétent",
                    "tbarkallah khdema nadya",
                    "seyed tbarkallah khdema met9ouna ",
                    "wa3er"
            };

            String reviewContent = reviewContents[random.nextInt(reviewContents.length)];
            int rating = 1 + random.nextInt(5); // 1-5 stars





            try {
                employee.addReview(reviewer, reviewContent, rating);
            } catch (InvalidPropertiesFormatException e) {
                System.err.println("Failed to add review: " + e.getMessage());
                // Handle the exception (e.g., log it or skip this review)
            }
        }
    }

    private  void generateJobs(List<Employer> employers, List<Employee>  employees) {
        // salaris
        // location
        // job status
        JobCategory[] categories = {
                JobCategory.PLUMBING ,
                JobCategory.ELECTRICAL,
                JobCategory.PAINTING,
                JobCategory.CARPENTRY,
                JobCategory.GARDENING,
                JobCategory.MAINTENANCE,
                JobCategory.MASONRY,
                JobCategory.CLEANING,
                JobCategory.MOVING,
                JobCategory.OTHER,
                JobCategory.PLUMBING,
                JobCategory.ELECTRICAL,
                JobCategory.PAINTING,
                JobCategory.CARPENTRY,
                JobCategory.GARDENING,
                JobCategory.MAINTENANCE,
                JobCategory.MASONRY,
                JobCategory.CLEANING,
                JobCategory.MOVING,
                JobCategory.OTHER,
                JobCategory.PLUMBING,
                JobCategory.ELECTRICAL,
                JobCategory.PAINTING,
                JobCategory.CARPENTRY,
                JobCategory.GARDENING,
                JobCategory.MAINTENANCE,
                JobCategory.MASONRY,
                JobCategory.CLEANING,
                JobCategory.MOVING,
                JobCategory.OTHER,
                JobCategory.PLUMBING,
                JobCategory.ELECTRICAL,
        };
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
                "Informaticien pour dépannage ordinateurs",
                "Soudeur pour chantier naval à Tanger",
                "Carreleur pour salle de bain en marbre",
                "Ébéniste pour restauration meubles anciens",
                "Pisciniste pour installation et entretien",
                "Chauffeur livreur avec camionnette",
                "Femme de ménage pour résidence à Marrakech",
                "Coiffeur à domicile pour cérémonie",
                "Professeur particulier en mathématiques",
                "Traducteur arabe-français pour documents",
                "Photographe professionnel pour événement",
                "Infirmier à domicile pour personne âgée",
                "Coach sportif personnel à Rabat",
                "Architecte d'intérieur pour appartement",
                "Décorateur événementiel pour mariage",
                "Mécanicien automobile spécialiste allemande",
                "Tailleur pour costumes traditionnels",
                "Serveur pour restaurant saisonnier",
                "Babysitter bilingue français-arabe",
                "Comptable pour petite entreprise",
                "Graphiste pour création logo"
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
                "Dépannage et mise à jour de plusieurs ordinateurs dans une petite entreprise.",
                "Soudure de pièces métalliques pour chantier portuaire. Expérience marine requise.",
                "Pose de carrelage en marbre italien pour salle de bain de luxe.",
                "Restauration de meubles anciens en bois de thuya selon méthodes traditionnelles.",
                "Installation et entretien mensuel de piscine hors-sol de 8m x 4m.",
                "Livraison de marchandises dans la région de Casablanca avec véhicule fourni.",
                "Ménage quotidien pour une villa de 5 chambres dans le quartier de l'Hivernage.",
                "Coiffure et maquillage pour mariée le jour de la cérémonie.",
                "Cours de soutien en mathématiques pour élève de terminale, 4h/semaine.",
                "Traduction de documents juridiques de l'arabe vers le français pour étude notariale.",
                "Couverture photographique complète d'un événement d'entreprise (200 photos minimum).",
                "Soins quotidiens et accompagnement pour personne âgée à domicile.",
                "Programme de remise en forme personnalisé avec 3 séances/semaine.",
                "Redesign complet d'un appartement de 80m2 dans le centre-ville de Rabat.",
                "Décoration complète pour mariage traditionnel marocain (200 invités).",
                "Réparation de véhicules allemands (BMW, Mercedes) avec pièces d'origine.",
                "Confection de caftans et costumes traditionnels sur mesure pour fête.",
                "Service en salle pour restaurant saisonnier à Agadir (juillet-août).",
                "Garde d'enfants bilingue avec aide aux devoirs, 5j/semaine.",
                "Gestion complète de la comptabilité pour PME (balance, TVA, paie).",
                "Création d'identité visuelle complète pour nouvelle entreprise."
        };


        for(Employer employer : employers){

            for(int j = 0 ; j< generateRandomInt(0,10) ; j++){
                JobStatus status = JobStatus.values()[new Random().nextInt(JobStatus.values().length)];

                Job job = new Job(
                        titles[ j % titles.length],
                        descriptions[j % descriptions.length],
                        JobCategory.values()[new Random().nextInt(JobCategory.values().length)],
                        status,
                        addresses.get(generateRandomInt(1,435)) ,
                        (float) (100 + generateRandomInt(1,1000)),  // 500-1500 DHS
                        // random date from this year
                        LocalDateTime.of(
                                LocalDateTime.now().getYear(),
                                generateRandomInt(1, 12),
                                generateRandomInt(1, 28),
                                generateRandomInt(0, 23),
                                generateRandomInt(0, 59)
                        )
                );
                // add the applicants and missions
                for(int k = 0 ; k < generateRandomInt(0,10) ; k++){
                    job.addApplicant(employees.get(generateRandomInt(0,employees.size()-1)).getId());
                    // maybe accept or reject a candidate
                    if(generateRandomInt(0,10) == 1){ // 10% chance to accept
                        job.acceptCandidate(employees.get(generateRandomInt(0,employees.size()-1)).getId());
                    }
                    else if(generateRandomInt(0,10) == 2){ // 10% chance to reject
                        job.rejectCandidate(employees.get(generateRandomInt(0,employees.size()-1)).getId());
                    }
                }
                for(int k = 0 ; k < generateRandomInt(0,10) ; k++){
                    job.addMission("Mission " + (k+1));
                }
                job.setEmployer(employer);
                employer.postJob(job);
            }

        }

    }

    // function to generate a random n character string
    public  String generateRandomString(int length) {
        StringBuilder sb = new StringBuilder();
        String characters = "ABCDEFGHIJKLMNOPQRSTU-VWXYZabcdefghijklmnopqrstuvwxyz0123456789";
        for (int i = 0; i < length; i++) {
            int index = (int) (Math.random() * characters.length());
            sb.append(characters.charAt(index));
        }
        return sb.toString();
    }

    // function to generate  random int between min and max
    public  int generateRandomInt(int min, int max) {
        return (int) (Math.random() * (max - min + 1)) + min;
    }

    // function to generate a random n digits string
    public  String generateRandomNumber(int length) {
        StringBuilder sb = new StringBuilder();
        String characters = "0123456789";
        for (int i = 0; i < length; i++) {
            int index = (int) (Math.random() * characters.length());
            sb.append(characters.charAt(index));
        }
        return sb.toString();
    }

    public  List<JobCategory> getRandomJobCategories(int size) {
            Random random = new Random();
            List<JobCategory> categories = new ArrayList<>();
            JobCategory[] allCategories = JobCategory.values();

            // Ensure the list size doesn't exceed the number of available categories
            int numberOfCategories = Math.min(size, allCategories.length);

            while (categories.size() < numberOfCategories) {
                JobCategory randomCategory = allCategories[random.nextInt(allCategories.length)];
                if (!categories.contains(randomCategory)) {
                    categories.add(randomCategory);
                }
            }

            return categories;
    }

    public  List<Skill> getRandomJobSkills(int size) {
        Random random = new Random();
        List<Skill> skills = new ArrayList<>();
        Skill[] allSkills = Skill.values();

        // Ensure the list size doesn't exceed the number of available categories
        int numberOfSkills = Math.min(size, allSkills.length);

        while (skills.size() < numberOfSkills) {
            Skill randomCategory = allSkills[random.nextInt(allSkills.length)];
            if (!skills.contains(randomCategory)) {
                skills.add(randomCategory);
            }
        }

        return skills;
    }



}