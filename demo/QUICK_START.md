# Quick Start Guide - Student Library Management System

## 🚀 Quick Start (5 Minutes)

### 1. Prerequisites Check

- Java 17+: Run `java -version`
- Maven 3.8+: Run `mvn -version`
- Gmail account with App Password enabled

### 2. Configure Email

Edit `src/main/resources/application.properties`:

```properties
spring.mail.username=your-email@gmail.com
spring.mail.password=YOUR_APP_PASSWORD  # 16-char password from Gmail
```

### 3. Build & Run

```bash
mvn clean install
mvn spring-boot:run
```

### 4. Access

- Open: `http://localhost:8080`
- Login with: `admin@library.com` / `Admin@123`
- Or create a new student account

---

## 📁 Project Structure Quick Reference

```bash
src/main/
├── java/com/example/demo/
│   ├── entity/           # Database models (User, Book, BookIssuance, LibraryAccessLog)
│   ├── repository/       # Data access interfaces (Spring Data JPA)
│   ├── service/          # Business logic (UserService, BookService, etc.)
│   ├── controller/       # REST & View controllers
│   │   ├── AuthController.java
│   │   ├── DashboardController.java
│   │   └── api/
│   └── config/           # Security, Email, Database config
│
├── resources/
│   ├── templates/        # HTML pages (Thymeleaf)
│   │   ├── auth/         # Login, Register, Verification pages
│   │   └── dashboard/    # Student and Admin dashboards
│   ├── static/           # Frontend assets
│   │   ├── css/          # Stylesheets
│   │   └── js/           # JavaScript
│   └── application.properties  # Configuration
```

---

## 🔑 Key Credentials

### Admin Account

- Email: `admin@library.com`
- Password: `Admin@123`

### Student Registration

- Go to registration page
- Fill form and submit
- Check email for verification link
- Click link to verify and login

---

## 📚 Main Entities

### User

- Email-based login
- Role: ADMIN or USER
- EmailVerified flag
- Session token tracking

### Book

- Title, Author, ISBN
- Category, Quantity
- Publication Year

### BookIssuance

- Links User → Book
- Tracks issuance date
- Stores email sent status

### LibraryAccessLog

- Entry/Exit timestamps
- Session token
- Duration calculation

---

## 🌐 API Quick Reference

### Authentication

```bash
POST /auth/login            - Login with email/password
POST /auth/register         - Create new account
GET /auth/verify?token=xxx  - Verify email
GET /auth/logout            - Logout
```

### Books

```bash
GET /api/books                      - All books
GET /api/books/available            - Books with quantity > 0
GET /api/books/search/title?q=...   - Search by title
GET /api/books/search/author?q=...  - Search by author
GET /api/books/categories           - Get all categories
```

### Issuances

```bash
POST /api/issuances/issue?bookId=1           - Issue single book
POST /api/issuances/issue-multiple           - Issue multiple books
GET /api/issuances/history                   - User's issuance history
POST /api/issuances/enter                    - Log library entry
POST /api/issuances/exit                     - Log library exit
GET /api/issuances/in-library                - Check if currently in library
```

---

## 🔧 Common Tasks

### Reset Database

```bash
# Stop the application
# Delete library.db file
# Restart application (new DB will be created)
```

### View Logs

```bash
# Logs appear in console during development
# Check application.properties for log level configuration
```

### Change Admin Password

1. Login to database (library.db)
2. Update User password (BCrypt hashed)
3. Or restart and let DataInitializer reset it

### Add Sample Books

Edit `DataInitializer.java` and add entries to the createSampleBooks() method before startup

---

## 🐛 Common Issues & Solutions

| Issue               | Solution                                                     |
| ------------------- | ------------------------------------------------------------ |
| Email not sending   | Check SMTP credentials, verify 2FA is enabled in Gmail       |
| 404 on static files | Ensure files are in `src/main/resources/static/`             |
| Login fails         | Verify email is verified, check credentials in admin account |
| Database locked     | Delete library.db and restart application                    |
| Port 8080 in use    | Change `server.port` in application.properties               |

---

## 📊 Dashboard Access

### Student Dashboard

- URL: `/dashboard/student`
- View: Browse books, track issuances, library status
- Auth: Requires ROLE_USER

### Admin Dashboard

- URL: `/dashboard/admin`
- View: Statistics, student list, issuances, access logs
- Auth: Requires ROLE_ADMIN
- Auto-refresh: Every 30 seconds

---

## 💾 Database Details

### Database File

- Location: `library.db` (project root)
- Type: SQLite
- Auto-creates on first run

### Pre-loaded Data

- 1 admin user
- 10 sample books
- No student users (must register)

### Access Directly

```bash
sqlite3 library.db
.tables                  # View tables
SELECT * FROM user;     # View users
SELECT * FROM book;     # View books
```

---

## 📧 Email Configuration

### Gmail Setup Steps

1. Enable 2-Factor Authentication on Google Account
2. Go to: myaccount.google.com → Security
3. Find "App Passwords" section
4. Select "Mail" and "Windows Computer" (or your device)
5. Google generates 16-character password
6. Copy password to `application.properties`

### Email Types Sent

- Registration verification (confirmation link)
- Book issuance confirmation (receipt with book list)

---

## 🎨 Frontend Structure

### CSS System

- `css/style.css`: Main styles, components, layout
- `css/animations.css`: Animations and transitions

### JavaScript

- `js/main.js`: API calls, interactive features

### Thymeleaf Integration

- Server-side templating
- Server variables: `${user}`, `${books}`, etc.
- Security tags: `sec:authorize`, `sec:authentication`

---

## 🔐 Security Overview

- **Authentication**: Spring Security with form login
- **Password**: BCrypt hashing
- **CSRF**: Enabled globally
- **Roles**: ROLE_ADMIN, ROLE_USER
- **Session**: 30-minute timeout
- **Email Verification**: Required for account activation

---

## 📦 Deployment Checklist

- [ ] Change admin password in DataInitializer
- [ ] Update email credentials in application.properties
- [ ] Set `spring.jpa.hibernate.ddl-auto=update` (production mode)
- [ ] Update `app.base-url` to production domain
- [ ] Build: `mvn clean package`
- [ ] Run: `java -jar demo-1.0.0.jar`

---

## 📞 Support Resources

1. **README.md** - Full documentation
2. **Code Comments** - Inline documentation
3. **Application Logs** - Debug information
4. **API Endpoints** - Self-documenting REST interface

---

## 🎯 Quick Test Flow

1. **Register**: Go to `/` → Register link → Fill form → Verify email
2. **Login**: Use new email/password
3. **Browse**: Browse available books
4. **Issue**: Select and issue a book
5. **Exit**: Log out (library exit automatically)
6. **Admin**: Login with admin account → View statistics

---

## 🚀 Next Steps

1. ✅ Configure email settings
2. ✅ Build and run application
3. ✅ Test registration/login flow
4. ✅ Browse books and issue
5. ✅ Check admin dashboard
6. ✅ Review logs and statistics

**Happy Library Managing!** 📚
