# Appointment Scheduling System - Phase 1

## Implemented user stories
- US1.1 Administrator login
- US1.2 Administrator logout
- US1.3 View available appointment slots
- US2.1 Book appointment
- US2.2 Enforce visit duration rule
- US2.3 Enforce participant limit
- US3.1 Send appointment reminders
- US4.1 Modify or cancel appointment
- US4.2 Manage reservations by administrator
- US5.1 Support multiple appointment types
- US5.2 Apply different rules per type

## Design choices
- **Layered design**: controllers -> services -> domain -> repository
- **Strategy Pattern**: booking rules are implemented through `BookingRuleStrategy`
- **Observer Pattern**: reminders and updates are sent through `Observer`
- **Persistence**: users use file storage, appointments use in-memory repository

## Default admin account
- Email: `admin@system.com`
- Password: `admin123`

## Run
```bash
mvn test
mvn javafx:run
```

## Notes
- All main classes contain Javadoc comments.
- JaCoCo report is generated after running tests.
- UML class diagram is available in `docs/UML_Class_Diagram.puml`.


## Gmail notification setup

This version supports sending real booking notifications to a user's Gmail account.

1. Keep the administrator account as-is:
   - Email: `admin@system.com`
   - Password: `admin123`
2. Create a file named `email-config.properties` in the project root (same folder as `pom.xml`).
3. Put your sender Gmail settings in that file:

```properties
mail.enabled=true
mail.username=yourprojectsender@gmail.com
mail.appPassword=YOUR_16_CHARACTER_GOOGLE_APP_PASSWORD
mail.host=smtp.gmail.com
mail.port=587
mail.fromName=Appointment Scheduling System
```

Important: Gmail sending needs a Google **App Password** from the sender account.
User sign-up now accepts only real Gmail addresses ending with `@gmail.com`, and booking notifications are sent through the observer when email sending is enabled.
