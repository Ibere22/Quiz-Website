# Quiz Website

ეს არის თანამედროვე, მრავალფუნქციური ვებ-აპლიკაცია, სადაც შეგიძლია შექმნა, ჩაატარო და გაუზიარო სხვებს quiz-ები. პროექტი აგებულია Java (Servlets, JSP, JSTL), MySQL და Maven-ზე.

---

## ძირითადი ფუნქციები

### მომხმარებლისთვის
- **რეგისტრაცია და ავტორიზაცია:** უსაფრთხო შესვლა, პაროლის hashing-ით.
- **Quiz-ის შექმნა:** შეგიძლია შექმნა quiz სხვადასხვა ტიპის კითხვებით (question-response, fill-in-the-blank, multiple-choice, picture-response). შეგიძლია დააყენო პარამეტრები, მაგალითად, შემთხვევითი რიგითობა, ერთ გვერდზე ყველა კითხვა, ან დაუყოვნებლივი პასუხის ჩვენება.
- **Quiz-ის გავლა:** შეგიძლია ჩაატარო quiz როგორც ჩვეულებრივ, ასევე practice რეჟიმში.
- **Achievements:** მიიღე achievements სხვადასხვა აქტივობისთვის (მაგალითად, პირველი quiz-ის შექმნა, 10 quiz-ის გავლა და ა.შ.) და ნახე შენი პროგრესი.
- **Friends სისტემა:** გაუგზავნე, მიიღე ან უარყავი მეგობრობის მოთხოვნები, ნახე შენი მეგობრები და მათი სტატისტიკა.
- **Messaging:** შიდა შეტყობინებების სისტემა (notes, friend request, quiz challenge).
- **Leaderboard:** ნახე კონკრეტული quiz-ის leaderboard-ები.
- **User Profile:** ნახე შენი და სხვა მომხმარებლების პროფილები, სტატისტიკითა და მიღწევებით.

### Admin-ისთვის
- **Admin Panel:** მართე მომხმარებლები, quiz-ები და announcements.
- **Announcement სისტემა:** გამოაქვეყნე announcements, რომლებიც გამოჩნდება მთავარ გვერდზე.
- **User Management:** დააწინაურე მომხმარებლები admin-ად ან წაშალე ისინი.
- **Quiz Management:** ნახე და წაშალე ნებისმიერი quiz.
- **Data Cleanup:** წაშალე ყველა quiz attempt ან announcement.

---

## Database Schema

- იხილე `database_schema.sql` ფაილი სრული სქემისთვის.
- ძირითადი ცხრილებია: `users`, `quizzes`, `questions`, `quiz_attempts`, `friendships`, `messages`, `achievements`, `announcements`.

---

## ტექნოლოგიები

- **Backend:** Java 24, Jakarta Servlet 6, JSP, JSTL
- **Frontend:** JSP, JSTL, CSS
- **Database:** MySQL
- **Build:** Maven (`pom.xml`)
- **Testing:** JUnit 5

---

## ინსტალაცია და გაშვება

### აუცილებელი პროგრამები

- Java 24+
- Maven 3.6+
- MySQL 8+
- (სურვილისამებრ) Tomcat 10+ ან სხვა Servlet 6-compatible container


### 1. Database-ის მომზადება

- შექმენი database და ცხრილები:

```sh
mysql -u root -p < database_schema.sql
```

- Default credentials არის `root`/`root` (`DbUtil.java` და `DatabaseConnection.java`-ში). შეცვალე საჭიროებისამებრ.

### 2. Build

```sh
./mvnw clean package
```
ან (Windows-ზე)
```sh
mvnw.cmd clean package
```

### 3. Deploy

- გაუშვი პირდაპირ IDE-დან (IntelliJ IDEA) Tomcat integration-ით.

### 4. გახსენი ბრაუზერში

- [http://localhost:8080/](http://localhost:8080/) (ან შენი პორტი).

---

## გამოყენება

- **დარეგისტრირდი** ან შედი სისტემაში.
- **შექმენი quiz-ები** და დაამატე კითხვები.
- **გაატარე quiz-ები** და ნახე შედეგები.
- **გაგზავნე მეგობრობის მოთხოვნები** და შეტყობინებები.
- **მიიღე achievements** და ნახე პროგრესი.
- **ნახე leaderboard-ები**.
- **Admin-ები:** შედი admin panel-ში შესაბამისი უფლებებით. (მიაწერე ხელით '/admin' url-ს ბოლოში, admin panel-ში შესვის გზა საჯარო არ უნდა იყოს)

---

## პროექტის სტრუქტურა

- `src/main/java/` - Java source (controller-ები, DAO-ები, model-ები, listener-ები, util-ები)
- `src/main/webapp/` - JSP-ები, static რესურსები, `WEB-INF`
- `src/test/java/` - Unit test-ები
- `database_schema.sql` - MySQL schema
- `pom.xml` - Maven dependencies და build კონფიგი

---

## მთავარი User Flow-ები

- **რეგისტრაცია/შესვლა:** `/register`, `/login`
- **Quiz-ების დათვალიერება:** `/quizzes`, `/quiz?id=...`
- **Quiz-ის შექმნა:** `/quiz/create`, `/quiz/addQuestion`
- **Profile:** `/profile` (საკუთარი), `/user?username=...` (სხვისი)
- **Friends:** `/friends`
- **Messages:** `/messages`
- **Leaderboard:** `/leaderboard`
- **Admin Panel:** `/admin`

---

## ტესტირება

- ყველა unit test-ის გასაშვებად:
  ```sh
  ./mvnw test
  ```
- ტესტები ფარავს DAO-ებს, model-ებს და ძირითად ლოგიკას.

---

## ავტორები

- FreeUni OOP Final Project Team -> hmm
- ნიკა სადღობელაშვილი, ირაკლი ბერელიძე, გიორგი სულაქველიძე, კონსტანტინე ბახუტაშვილი

