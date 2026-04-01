# Job Portal Application

A comprehensive web-based job portal built with Spring Boot, allowing job seekers to find opportunities and employers to post job listings.

## Table of Contents
- [Features](#features)
- [Technology Stack](#technology-stack)
- [Project Structure](#project-structure)
- [Installation & Setup](#installation--setup)
- [User Roles](#user-roles)
- [Core Functionality](#core-functionality)
- [API Endpoints](#api-endpoints)
- [Database Schema](#database-schema)
- [Security](#security)
- [File Upload](#file-upload)
- [Premium Features](#premium-features)

## Features

### For Job Seekers
- User registration and authentication
- Profile management with image upload
- Job search and filtering by category, location, salary
- Job application with resume upload
- Application tracking dashboard
- Premium subscription for unlimited applications

### For Employers
- Company profile management with logo upload
- Job posting with rich descriptions
- Application management and review
- Resume download functionality
- Premium subscription for unlimited job postings

### For Admins
- User management
- Job oversight
- System analytics

## Technology Stack

- **Backend**: Spring Boot 2.x
- **Frontend**: Thymeleaf, HTML5, CSS3, JavaScript
- **Database**: JPA/Hibernate (configurable)
- **Security**: Spring Security
- **Styling**: Bootstrap 5, Font Awesome 6
- **File Upload**: Multipart file handling
- **Build Tool**: Maven

## Project Structure

```
src/
├── main/
│   ├── java/com/jobportal/
│   │   ├── config/
│   │   │   ├── DataInitializer.java
│   │   │   └── WebConfig.java
│   │   ├── controllers/
│   │   │   ├── AdminController.java
│   │   │   ├── AuthController.java
│   │   │   ├── DashboardController.java
│   │   │   ├── HomeController.java
│   │   │   ├── JobApplicationController.java
│   │   │   ├── JobController.java
│   │   │   └── PaymentController.java
│   │   ├── models/
│   │   │   ├── Job.java
│   │   │   ├── JobApplication.java
│   │   │   ├── Role.java
│   │   │   └── User.java
│   │   ├── repositories/
│   │   │   ├── JobApplicationRepository.java
│   │   │   ├── JobRepository.java
│   │   │   ├── RoleRepository.java
│   │   │   └── UserRepository.java
│   │   ├── security/
│   │   │   ├── services/UserDetailsServiceImpl.java
│   │   │   └── WebSecurityConfig.java
│   │   ├── services/
│   │   │   ├── JobApplicationService.java
│   │   │   ├── JobService.java
│   │   │   └── UserService.java
│   │   └── JobPortalApplication.java
│   └── resources/
│       ├── static/
│       │   ├── css/styles.css
│       │   └── js/main.js
│       ├── templates/
│       │   ├── admin/
│       │   ├── applications/
│       │   ├── dashboard/
│       │   ├── jobs/
│       │   ├── profile/
│       │   └── layout.html
│       └── application.properties
└── uploads/
    ├── profiles/
    ├── logos/
    └── resumes/
```

## Installation & Setup

1. **Prerequisites**
   - Java 11 or higher
   - Maven 3.6+
   - Database (MySQL/PostgreSQL/H2)

2. **Clone Repository**
   ```bash
   git clone <repository-url>
   cd JobsWebsite/resources
   ```

3. **Database Configuration**
   Update `application.properties`:
   ```properties
   spring.datasource.url=jdbc:mysql://localhost:3306/jobportal
   spring.datasource.username=your_username
   spring.datasource.password=your_password
   spring.jpa.hibernate.ddl-auto=update
   ```

4. **Build & Run**
   ```bash
   mvn clean install
   mvn spring-boot:run
   ```

5. **Access Application**
   - URL: http://localhost:8080
   - Default Admin: admin@jobportal.com / admin123

## User Roles

### Job Seeker (USER)
- **Registration**: `/register/jobseeker`
- **Limitations**: 5 free job applications
- **Premium**: Unlimited applications for ₹499/month

### Employer (EMPLOYER)
- **Registration**: `/register/employer`
- **Limitations**: 3 free job postings
- **Premium**: Unlimited postings for ₹999/month

### Administrator (ADMIN)
- **Access**: Full system control
- **Features**: User management, job oversight

## Core Functionality

### Authentication & Authorization
- Spring Security implementation
- Role-based access control
- Session management
- Password encryption

### Job Management
- **Create**: Rich job descriptions with requirements/benefits
- **Search**: Keyword, location, category filtering
- **Sort**: By date, title, salary
- **Status**: Active, Closed, Draft

### Application Process
- **Apply**: Cover letter + resume upload
- **Track**: Application status updates
- **Manage**: Employer application review

### File Upload System
- **Profile Images**: User avatars
- **Company Logos**: Employer branding
- **Resumes**: PDF/DOC support
- **Storage**: Local file system with UUID naming

## API Endpoints

### Public Endpoints
```
GET  /                    - Home page
GET  /jobs               - Job listings
GET  /jobs/search        - Job search
GET  /jobs/{id}          - Job details
POST /login              - User login
POST /register           - User registration
```

### Authenticated Endpoints
```
GET  /dashboard          - User dashboard
GET  /profile            - User profile
POST /profile/update     - Update profile
GET  /jobs/{id}/apply    - Job application
POST /logout             - User logout
```

### Employer Endpoints
```
GET  /employer/jobs/create     - Job creation form
POST /employer/jobs/create     - Create job
GET  /employer/jobs            - Employer job list
POST /employer/jobs/{id}/close - Close job
POST /employer/jobs/{id}/delete - Delete job
```

### Admin Endpoints
```
GET  /admin/users        - User management
GET  /admin/jobs         - Job management
```

## Database Schema

### Users Table
```sql
- id (Primary Key)
- firstName, lastName
- email (Unique)
- password (Encrypted)
- userType (JOB_SEEKER, EMPLOYER, ADMIN)
- profileImage, companyLogo
- isPremium, premiumExpiresAt
- jobsAppliedCount, jobsPostedCount
- createdAt, updatedAt
```

### Jobs Table
```sql
- id (Primary Key)
- title, description
- location, salary
- jobType (FULL_TIME, PART_TIME, CONTRACT, INTERNSHIP, REMOTE)
- status (ACTIVE, CLOSED, DRAFT)
- employer_id (Foreign Key)
- createdAt, updatedAt
```

### Job Applications Table
```sql
- id (Primary Key)
- job_id (Foreign Key)
- applicant_id (Foreign Key)
- coverLetter, resumePath
- status (APPLIED, REVIEWING, SHORTLISTED, INTERVIEWED, REJECTED, OFFERED, HIRED)
- createdAt
```

## Security

### Authentication
- Spring Security configuration
- BCrypt password encoding
- Session-based authentication
- CSRF protection enabled

### Authorization
- Role-based access control
- Method-level security
- Template-level security with Thymeleaf

### File Security
- UUID-based file naming
- Access control for resume downloads
- File type validation

## File Upload

### Configuration
- Max file size: 10MB
- Supported formats: PDF, DOC, DOCX (resumes), JPG, PNG, GIF (images)
- Storage location: `uploads/` directory

### File Structure
```
uploads/
├── profiles/     - User profile images
├── logos/        - Company logos
└── resumes/      - Job application resumes
```

## Premium Features

### Job Seekers (₹499/month)
- Unlimited job applications
- Priority application status
- Advanced search filters
- Email notifications

### Employers (₹999/month)
- Unlimited job postings
- Priority support
- Enhanced company profile
- Application analytics

## Styling & UI

### Theme
- Modern dark theme with unified color scheme
- Primary: Dark gray (#1f2937)
- Secondary: Medium gray (#374151)
- Success: Green (#10b981)
- Danger: Red (#ef4444)

### Components
- Responsive Bootstrap 5 layout
- Modern Font Awesome 6 icons
- Custom CSS with CSS variables
- Smooth transitions and hover effects

## Development Notes

### Key Design Decisions
- Server-side rendering with Thymeleaf
- File-based storage for simplicity
- Role-based premium limitations
- Unified dark theme for modern appearance

### Future Enhancements
- Email notifications
- Advanced search filters
- Company reviews
- Salary insights
- Mobile app API

## Support & Maintenance

### Logging
- Application logs for debugging
- Error handling with user-friendly messages
- Security audit trails

### Monitoring
- Application health endpoints
- Database connection monitoring
- File upload tracking

---

**Version**: 1.0  
**Last Updated**: 2025  
**License**: Proprietary