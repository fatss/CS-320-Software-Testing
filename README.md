# CS-320-Software-Testing

SNHU CS 320 Software Testing, Automation, and Quality Assurance Portfolio

This portfolio contains the Contact Service from Project One and the Summary
and Reflections report from Project Two. The Contact Service is a Java
back-end component for the Grand Strand Systems mobile application. It is
tested with JUnit 5, and JaCoCo measures coverage. The report analyzes the
testing approach, the techniques applied, and the mindset behind them.

## Files

```text
ContactService/
  Contact.java              Contact object with field validation
  ContactService.java       In-memory service to add, delete, and update contacts
  ContactTest.java          Unit tests for Contact
  ContactServiceTest.java   Unit tests for ContactService
Project_Two_Summary_and_Reflections_Report.docx   Project Two report
```

## Requirements

- **Contact ID:** unique, non-null, at most 10 characters, and cannot be
  changed.
- **First name and last name:** non-null, at most 10 characters each.
- **Phone:** non-null, exactly 10 digits (leading zeros are kept).
- **Address:** non-null, at most 30 characters.
- **Service:** adds contacts with unique IDs, deletes by ID, and updates first
  name, last name, phone, and address by ID.

Invalid values, null contacts, duplicate IDs, and operations on unknown IDs
throw `IllegalArgumentException`. A rejected update leaves the stored contact
unchanged.

## Test results

26 test cases (16 in `ContactTest`, 10 in `ContactServiceTest`, counting each
parameterized run separately) passed with 0 failures. Coverage:

| Class | Lines | Branches |
| --- | ---: | ---: |
| Contact | 30 / 30 (100%) | 20 / 20 (100%) |
| ContactService | 22 / 22 (100%) | 8 / 8 (100%) |

## Reflection

### How can I ensure that my code, program, or software is functional and secure?

I make code functional by writing down what correct behavior looks like and
then testing against it. For the Contact Service, each requirement became tests
on both sides of its limit. A first name of exactly 10 characters must be
accepted and one of 11 must be rejected. The phone tests cover null, empty,
short, long, alphabetic, signed, whitespace-containing, and full-width-digit
values, and they check that a leading zero in "0123456789" is kept. I learned
that a test only adds confidence when it checks the outcome and the state left
behind. When a duplicate ID is rejected, the test uses `assertSame` to confirm
the original contact is still the one stored. When an invalid update is
rejected, the test confirms the old values did not change. The suite reaches
100% line and branch coverage. Coverage alone does not prove correctness,
though. It only shows which code ran, so I read it together with how strong the
assertions are.

Security starts with the same habits: validating input and protecting internal
data. Every constructor and setter rejects null or oversized values before it
changes a field. The phone check uses a strict pattern that only accepts the
digits 0 to 9. The contact ID is `final`, so a record's identity cannot change
after it is created. `ContactService` uses `putIfAbsent`, so a duplicate ID can
never overwrite an existing contact. These checks are not the whole picture,
though. A deployed version would also need authorization (so one user cannot
read another user's contacts), protection of personal data, concurrency
testing, and security testing based on realistic threats.

### How do I interpret user needs and incorporate them into a program?

I treat the customer's requirements as a contract and turn each rule into
something I can check. Each rule, such as "non-null, at most 10 characters, not
updatable," became a validation check in `Contact` and a set of tests that
prove it. I also tried not to add requirements the customer never stated. The
names and address only needed to be non-null and within a length limit, so I
allowed empty strings, even though I would personally expect a name to be
filled in. My own expectations should not quietly replace the agreed
requirement. When a requirement is unclear, the right step is to ask the
customer, not to change a test so it matches whatever the code currently does.
Because I wrote both the code and the tests, the same misunderstanding could
end up in both. A peer deriving test cases independently from the requirements
would help catch that.

### How do I approach designing software?

I start by breaking the problem into small parts with clear responsibilities.
`Contact` guards its own fields, and `ContactService` manages the collection of
contacts. Validation lives in one place: the constructor calls the same setters
that updates use, so each rule is written only once. The service stores
contacts in an in-memory `HashMap`, which keeps it easy to test now. Its public
methods would stay the same if the map were later replaced with a database. I
design with testing in mind: small, predictable methods, clear exceptions for
invalid input, and no hidden dependencies, so each behavior can be checked in
isolation. In the tests, `@BeforeEach` creates a fresh service for every test,
and parameterized tests cover many inputs without repeating code. I also try to
be honest about what a design does not cover yet, and I record those gaps as
future work. Here, that includes integration tests with a real database,
concurrency tests for simultaneous requests, and security tests once users and
a network layer are added.
