# DevFlow AI Security Specifications

DevFlow AI implements enterprise-level security designs to protect data integrity, prevent unauthorized resource access, and mitigate common application attacks.

---

## 1. Authentication & Token Filters
- **Stateless Authorization**: Utilizes JSON Web Tokens (JWT) signed using the HMAC-SHA-256 algorithm.
- **BCrypt Hashing**: User passwords are encrypted using BCrypt with a default work factor of 10. Passwords are never stored in raw text, nor are hashes returned in REST DTO payloads.
- **Exception Protection**: Token validations in `JwtAuthenticationFilter` are wrapped in try-catch blocks to catch malformed, expired, or mismatch signature exceptions, logging a warning rather than crashing requests.

---

## 2. Resource Boundaries & Data Isolation (Ownership)
- **Collaborator Validation**: Access to projects, tasks, and notes is strictly restricted. The service layer verifies that the authenticated user's email either matches the project owner's email or matches a record in the `ProjectMember` collaborator list.
- **Prevent Path Traversal**: Files uploaded to the attachments workspace are assigned a randomized UUID prefix. Filenames are cleaned using `Paths.get(originalName).getFileName().toString()`, neutralizing traversal payloads (e.g. `../../etc/passwd`).

---

## 3. Web Protections & CORS Configuration
- **Cross-Origin Resource Sharing (CORS)**: Access is explicitly constrained. The Angular application origin `http://localhost:4300` is whitelisted with credential permissions enabled, preventing arbitrary wildcard cross-site calls.
- **Cross-Site Scripting (XSS)**: Standalone Angular HTML templates bind variables dynamically using native sanitization pipelines to prevent malicious scripts from execution inside user views.
- **CORS Pre-Flight**: Spring Security chain explicitly maps `OPTIONS` request methods to permit pre-flight handshakes from the frontend interceptor.
