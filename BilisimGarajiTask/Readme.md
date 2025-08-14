BilisimGarajiTask

Fatihhan Baysal 14/08/2025
Roller: SuperAdmin, Teacher, Student.  
Tüm CRUD’lar sadece SuperAdmin tarafından yapılır.  
Docker Compose ile Postgres+Redis+App birlikte kalkar.
Testler Swagger UI üzerinden yapılabilir.
Tüm ID’ler UUID olmalıdır.   
Kurulum  
1- Paketle  
mvn -DskipTests clean package  (docker içerisinde)
2- Docker ile ayağa kaldır  
docker compose up -d --build  
3- Swagger UI  
URL: http://localhost:8080/swagger-ui.html (API docs: /api-docs).

Proje Docker ortamında çalışır ve Swagger’dan test edilebilir. 

4- DB’ye GUI ile bağlanmak  

Host: localhost, Port: 5433, DB: lms_db, User: postgres, Pass: 1234.

1) Swagger’da Bearer Auth’u Hazırlama (Token alma → Authorize)

İlk SuperAdmin’i oluştur (DB boşken)

POST /api/v1/auth/user-register
Body (JSON):

{
"email": "admin@bg.local",
"password": "P@ssw0rd!",
"firstName": "Super",
"lastName": "Admin"
}

password minimum 4 karakterle sınırlandırıldı.

Senaryo: Sistem boş başlar; ilk kullanıcı userRegister ile oluşturulur. (SuperAdmin)

1.2) Login olup access/refresh token al

POST /api/v1/auth/login

Body:

{
"email": "admin@bg.local",
"password": "P@ssw0rd!"
}

Response:

{
"accessToken": "eyJhbGciOiJIUzI1NiIs...",
"refreshToken": "eyJhbGciOiJIUzI1NiIs..."
}

Swagger’da sağ üstteki Authorize butonuna tıklayıp Bearer alanına Bearer <accessToken> şeklinde yapıştır.


POST /api/v1/auth/refresh-token

{ "refreshToken": "<refresh>" }

Response:
{ "accessToken": "...", "refreshToken": "..." }


2)Senaryoya Göre Uçtan Uca Akış (SuperAdmin ile)

Brand → Organization → Classroom → Users → Assignments → Courses → Assign Course to Classroom. (Öğretmen/Öğrenci görünürlük kısıtları akabinde test edilir.) 

2.1) Brand oluştur (Kurum); kod sistemce atanır

POST /api/v1/brands

Body:

{
"name": "Bilisimgaraji",
"description": "Main brand",
"active": true
}

“Kurum kodları sistem tarafından otomatik atanır.” (İstekten gelmez.)

GET /api/v1/brands ile listeyi görebilirsin.

2.2) Organization oluştur (Brand’a bağlı)

POST /api/v1/organizations

Body:

{
"brandId": "BRAND_UUID",
"name": "Ankara Org",
"description": "HQ Organization",
"active": true
}

GET /api/v1/organizations?brandId=BRAND_UUID ile liste.

2.3) Classroom oluştur (Organization’a bağlı)

POST /api/v1/classrooms

Body:

{
"organizationId": "ORG_UUID",
"name": "10-A",
"description": "First Class",
"active": true
}

Sınıflar organizasyonlara bağlıdır; öğrenciler sınıflara atanır.

2.4) Kullanıcı oluştur – Teacher

POST /api/v1/users

Body (Teacher):

{
"email": "teacher1@bg.local",
"password": "P@ssw0rd!",
"firstName": "T",
"lastName": "One",
"role": "TEACHER",
"profile_id": 1,
"active": true
}


users POST’unda profile_id (1=Teacher, 2=Student) bekler. Biz ayrıca role alanını da kullanıyoruz fakat doldurulması gerekmez. 

2.5) Kullanıcı oluştur – Student (org + class zorunlu)

POST /api/v1/users

Body (Student):

{
"email": "student1@bg.local",
"password": "P@ssw0rd!",
"firstName": "S",
"lastName": "One",
"role": "STUDENT",
"profile_id": 2,
"organizationId": "ORG_UUID",
"classroomId": "CLASS_UUID",
"active": true
}

Öğrenciler doğrudan organizasyon ve sınıfa atanır; ders erişimi sınıf bazlı sağlanır.

2.6) Öğretmeni sınıfa ata

POST /api/v1/teachers/assign-classroom

Body:

{
"teacherId": "TEACHER_UUID",
"classroomId": "CLASS_UUID",
"active": true
}


Öğretmenler birden fazla sınıfa atanabilir; öğretmene ders ataması yapılmaz, ders sınıfa atanır.

2.7) Ders oluştur (Course)

POST /api/v1/courses

Body:

{
"name": "Matematik",
"code": "MATH-101",
"description": "Temel matematik",
"active": true
}

Dersler sistemde tanımlanmalı ve sınıflara atanmalıdır; aynı ders birden fazla sınıfa atanabilir.

2.8) Dersi sınıfa ata

POST /api/v1/courses/assign

Body:

{
"classroomId": "CLASS_UUID",
"courseId": "COURSE_UUID",
"active": true
}

Dersler sınıfa atanır; öğrenci derslere sınıfı üzerinden erişir.

3)Role Göre “Kendi” Uçları (Teacher & Student)

3.1) Teacher görünürlüğü

GET /api/v1/teachers/my-classes

GET /api/v1/teachers/my-students

GET /api/v1/teachers/my-courses

Nasıl test edilir?

Teacher’ı /auth/login ile giriş yap (teacher email+şifre).

Swagger’da Authorize → Teacher access token’ını gir.

“my-*” uçlarını çağır: sadece öğretmene atanmış sınıflar ve o sınıfların öğrenci/dersleri görünmeli.

3.2) Student görünürlüğü

GET /api/v1/students/my-courses

Nasıl test edilir?

Student ile /auth/login yap.

Swagger’da Authorize → Student access token’ı gir.

my-courses: yalnızca kendi sınıfına atanmış dersler gelmeli.

4)Swagger’da Formları Doğru Doldurmak (Mini Rehber)

UUID alanları
(brandId/organizationId/classroomId/teacherId/courseId):

Daha önceki create’lerden dönen id’leri kullan.

Yanlış/boş UUID → 400/404 hatası beklenir 

Users POST:

Teacher → profile_id=1, role=TEACHER.

Student → profile_id=2, role=STUDENT ve organizationId + classroomId zorunlu.

Assign uçları:

Teacher ↔ Classroom: teacherId + classroomId.  

Course ↔ Classroom: courseId + classroomId.

Brand code:

Servis otomatik üretir; formda code istemez.

5)Hata Mesajları (Global Handler Beklentisi)

400 Bad Request: Validasyon hataları (ör. boş alan, format).

403 Forbidden: Token var ama rol yetmez.

404 Not Found: ID’ler yanlış.

409 Conflict: Benzersiz alan çakışması (ör. aynı isim/kod).

500 Internal Server Error: Beklenmeyen durum.

Silme ve Güncelleme Kuralları service katmanında; global handler anlamlı mesaj döndürür.

6)Teknik Gereklilikler – Doğrulama Listesi

Swagger UI ile tüm uçlar test edilebilir.

docker-compose ile Postgres, Redis, App birlikte.

README: kurulum, Docker komutları, Swagger URL, örnek kullanıcı vb. içerir.

RBAC/JWT, UUID, MVC, Validation, Global Exception.

Kısa Özet Akış (10 Adımda Bitir)

1- /auth/user-register → İlk SuperAdmin’i aç.  
2-/auth/login → Access token al, Swagger Authorize yap.  
3-/brands (POST) → Brand oluştur (kod otomatik).  
4-/organizations (POST) → Brand’a bağlı org oluştur.  
5-/classrooms (POST) → Org’a bağlı sınıf oluştur.   
6-/users (POST) → Teacher oluştur (profile_id=1).  
7-/users (POST) → Student oluştur (profile_id=2, org+class zorunlu).   
8-/teachers/assign-classroom (POST) → Teacher’ı sınıfa ata.  
9-/teachers/assign-classroom (POST) → Teacher’ı sınıfa ata.   
10-/courses/assign (POST) → Dersi sınıfa ata; sonra Teacher/Student login ile my- uçlarını* test et. 





