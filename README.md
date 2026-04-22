# Student Library Access Management System

A modern, full-featured Spring Boot application that manages student access to a library, including book issuance tracking, login/logout management, and comprehensive admin oversight.

## 🎯 Features

### For Students

- **User Registration**: Create an account with email verification
- **Book Browsing**: Browse and search library books by title, author, or category
- **Book Issuance**: Issue multiple books at once with confirmation emails
- **Library Entry/Exit Tracking**: Track when you enter and exit the library
- **Issuance History**: View all previously issued books
- **Email Confirmations**: Receive confirmation emails when books are issued

### For Administrators

- **Admin Dashboard**: Comprehensive dashboard with real-time statistics
- **Student Monitoring**: Track which students are currently in the library
- **Issuance Logs**: View all book issuances with student and timestamp details
- **Access Logs**: See library entry and exit times for all students
- **System Statistics**: Monitor total students, books, and issuances

## 🏗️ Architecture

### Technology Stack

- **Backend**: Spring Boot 3.5.13
- **Language**: Java 17
- **Database**: SQLite
- **Frontend**: HTML5, CSS3, JavaScript
- **ORM**: JPA/Hibernate with Spring Data
- **Security**: Spring Security with form-based authentication
- **Email**: Spring Mail (SMTP)

### Project Structure

```bash
demo/
├── src/
│   ├── main/
│   │   ├── java/com/example/demo/
│   │   │   ├── entity/              # JPA Entities
│   │   │   ├── repository/          # Data access layer
│   │   │   ├── service/             # Business logic
│   │   │   ├── controller/          # Request handlers
│   │   │   │   ├── api/             # REST API controllers
│   │   │   │   └── ...
│   │   │   ├── config/              # Configuration classes
│   │   │   └── DemoApplication.java
│   │   ├── resources/
│   │   │   ├── templates/           # Thymeleaf HTML templates
│   │   │   │   ├── auth/
│   │   │   │   └── dashboard/
│   │   │   ├── static/
│   │   │   │   ├── css/             # Stylesheets
│   │   │   │   └── js/              # JavaScript files
│   │   │   └── application.properties
│   └── test/
├── pom.xml                          # Maven configuration
├── mvnw / mvnw.cmd                 # Maven Wrapper
└── library.db                       # SQLite Database (auto-created)
```

## 🚀 Getting Started

### Prerequisites

- Java 17 or higher
- Maven 3.8.1 or higher
- Gmail account with App Password (for email functionality)

### Installation & Setup

1. **Clone/Extract the Project**

   ```bash
   cd demo
   ```

2. **Configure Email Settings**

   Edit `src/main/resources/application.properties`:

   ```properties
   # Gmail Configuration
   spring.mail.username=your-email@gmail.com
   spring.mail.password=your-app-password
   ```

   To get Gmail App Password:
   - Enable 2-Factor Authentication on your Google Account
   - Go to Google Account → Security → App Passwords
   - Generate a new app password for "Mail"
   - Use this 16-character password in the configuration

3. **Build the Project**

   ```bash
   mvn clean install
   ```

4. **Run the Application**

   ```bash
   mvn spring-boot:run
   ```

   Or run the JAR file:

   ```bash
   mvn clean package
   java -jar target/demo-1.0.0.jar
   ```

5. **Access the Application**
   - Open browser and navigate to: `http://localhost:8080`
   - You will be redirected to the login page

## 📝 Default Credentials

### Admin Account

- **Email**: `admin@library.com`
- **Password**: `Admin@123`

### Student Registration

- Create a new account on the registration page
- Verify your email through the verification link sent to your inbox
- Login and start using the system

## 💾 Database

The application uses SQLite, which automatically creates a `library.db` file in the project root on first run.

### Pre-loaded Sample Data

The system automatically initializes with:

- Admin user account
- 10 sample books with various genres:
  - The Catcher in the Rye
  - To Kill a Mockingbird
  - Clean Code
  - The Design of Everyday Things
  - Atomic Habits
  - Thinking, Fast and Slow
  - A Brief History of Time
  - The Lean Startup
  - The Great Gatsby
  - Sapiens

## 🔐 Security Features

- **Password Encryption**: BCrypt hashing for all passwords
- **CSRF Protection**: Spring Security CSRF tokens
- **Email Verification**: Required for account activation
- **Role-Based Access Control**: ROLE_ADMIN and ROLE_USER
- **Session Management**: Automatic session timeout after 30 minutes
- **Form-Based Authentication**: Secure login mechanism

## 📱 API Endpoints

### Authentication

- `POST /auth/login` - User login
- `POST /auth/register` - User registration
- `GET /auth/verify?token={token}` - Email verification
- `GET /auth/logout` - User logout

### Books

- `GET /api/books` - Get all books
- `GET /api/books/available` - Get available books only
- `GET /api/books/{id}` - Get book by ID
- `GET /api/books/search/title?q={query}` - Search by title
- `GET /api/books/search/author?q={query}` - Search by author
- `GET /api/books/categories` - Get all categories

### Book Issuances

- `POST /api/issuances/issue?bookId={id}` - Issue single book
- `POST /api/issuances/issue-multiple` - Issue multiple books
- `GET /api/issuances/history` - Get issuance history
- `POST /api/issuances/enter` - Log library entry
- `POST /api/issuances/exit` - Log library exit
- `GET /api/issuances/in-library` - Check if user is in library

## 🎨 UI/UX Features

### Modern Design

- Gradient backgrounds with smooth animations
- Responsive grid layouts
- Floating card hover effects
- Smooth transitions and fade-ins
- Interactive buttons with visual feedback
- Loading spinners for async operations
- Toast notifications for user feedback

### CSS Animations

- FadeIn, FadeInUp, FadeInDown
- SlideIn animations (left, right, up, down)
- Scale animations
- Float and bounce effects
- Pulse animations
- Spinner/loader animations
- Staggered item animations

### Responsive Design

- Mobile-first approach
- Breakpoints for tablets and desktops
- Mobile navigation menu toggle
- Optimized layouts for all screen sizes

## 🔄 User Flow

### Student Journey

1. **Registration**: Create account → Verify email
2. **Login**: Enter credentials → Access dashboard
3. **Browse**: Search books by various filters
4. **Issue**: Select books → Click "Issue" → Receive confirmation email
5. **Track**: View issued books and library activity
6. **Exit**: Log out and exit the library

### Admin Journey

1. **Login**: Use admin credentials
2. **Dashboard**: View real-time statistics
3. **Monitor**: Check currently logged-in students
4. **Review**: View all issuances and access logs
5. **Track**: Monitor library activity and book circulation

## 🛠️ Configuration

### Application Properties

Located in `src/main/resources/application.properties`

```properties
# Server
server.port=8080

# Database
spring.datasource.url=jdbc:sqlite:library.db
spring.jpa.hibernate.ddl-auto=create-drop  # or 'update' for production

# Email
spring.mail.host=smtp.gmail.com
spring.mail.port=587
spring.mail.username=your-email@gmail.com
spring.mail.password=your-app-password

# Admin Credentials
app.admin.email=admin@library.com
app.admin.password=Admin@123

# Application Settings
app.base-url=http://localhost:8080
app.mail.from=noreply@studentlibrary.com
```

## 📊 Database Schema

### Users Table

Stores user information and authentication details

- id, name, email, password, role, emailVerified, isLoggedIn, sessionToken

### Books Table

Library book catalog

- id, title, author, isbn, description, quantity, category, publicationYear, publisher

### BookIssuances Table

Tracks book checkouts

- id, userId, bookId, issuanceDate, emailSentAt

### LibraryAccessLogs Table

Tracks library entry/exit

- id, userId, entryTime, exitTime, sessionToken

### EmailVerificationTokens Table

Manages email verification

- id, userId, token, createdAt, expiryDate, isUsed

## 🧪 Testing

### Test Coverage

- Unit tests for services
- Integration tests for controllers
- Spring Security tests with mock users

### Run Tests

```bash
mvn test
```

## 📚 API Examples

### Register New User

```bash
curl -X POST http://localhost:8080/auth/register \
  -H "Content-Type: application/x-www-form-urlencoded" \
  -d "name=John Doe&email=john@example.com&password=Password@123"
```

### Login

```bash
curl -X POST http://localhost:8080/auth/login \
  -H "Content-Type: application/x-www-form-urlencoded" \
  -d "username=john@example.com&password=Password@123"
```

### Get All Books

```bash
curl -X GET http://localhost:8080/api/books \
  -H "Authorization: Bearer YOUR_TOKEN"
```

### Issue a Book

```bash
curl -X POST "http://localhost:8080/api/issuances/issue?bookId=1" \
  -H "Authorization: Bearer YOUR_TOKEN"
```

### Enter Library

```bash
curl -X POST http://localhost:8080/api/issuances/enter \
  -H "Authorization: Bearer YOUR_TOKEN"
```

## 🐛 Troubleshooting

### Email Not Sending

- Verify Gmail credentials are correct
- Check that 2FA is enabled and App Password is generated
- Ensure SMTP settings in application.properties are correct
- Check internet connection

### Database Errors

- Delete `library.db` file to reset the database
- Check that SQLite JDBC driver is properly installed
- Verify file permissions in the project directory

### Authentication Issues

- Clear browser cookies and cache
- Verify email is verified before login
- Check that admin credentials are correct
- Ensure Spring Security configuration is loaded

### Static Resources Not Loading

- Verify files are in `src/main/resources/static/`
- Check file permissions
- Clear browser cache
- Restart the application

## 🚢 Deployment

### Build Production JAR

```bash
mvn clean package -DskipTests
```

### Deploy on Server

```bash
# Copy the JAR to server
scp target/demo-1.0.0.jar user@server:/app/

# SSH into server
ssh user@server

# Run the application
java -jar /app/demo-1.0.0.jar
```

### Environment Variables

```bash
export MAIL_USERNAME=your-email@gmail.com
export MAIL_PASSWORD=your-app-password
export SPRING_PROFILES_ACTIVE=production

java -jar demo-1.0.0.jar
```

## 📈 Performance Optimization

- Admin dashboard auto-refreshes every 30 seconds
- Lazy loading for book grids
- Efficient database queries with indexed fields
- Session timeout management
- Frontend caching of static assets

## 🔮 Future Enhancements

- Book renewal system
- Fine/penalty management
- Reading history and recommendations
- Mobile app integration
- Advanced analytics and reporting
- Notification system (SMS/Push)
- QR code for book identification
- Integration with institutional systems

## 📄 License

This project is open-source and available for educational purposes.

## 👨‍💼 Support

For issues, questions, or suggestions, please refer to the code documentation or contact the development team.

## 🙏 Acknowledgments

Built with Spring Boot, Thymeleaf, and modern web technologies to provide a seamless library management experience.

---

**Version**: 1.0.0  
**Last Updated**: April 2026  
**Built with ❤️ for better library management**
