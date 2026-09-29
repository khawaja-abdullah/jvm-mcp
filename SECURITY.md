# Security Policy

## Supported Versions

Only the latest release and active development branch receive security updates:

| Version | Supported |
| ------- | --------- |
| 1.0.x   | Yes       |
| < 1.0   | No        |

---

## Reporting a Vulnerability

We take security seriously. If you discover a security vulnerability in `jvm-mcp` (such as improper privilege escalation during JVM attachment or unintended arbitrary code execution in the diagnostic agent), please follow responsible disclosure:

1. **Do not open a public GitHub issue.**
2. Report the vulnerability privately via **GitHub Private Vulnerability Reporting** on this repository:
   - Navigate to **Security** → **Advisories** → **Report a vulnerability**.
   - Alternatively, email the maintainer directly at `oscarbolanos09@gmail.com` with the subject `[SECURITY] JVM-MCP Vulnerability Report`.
3. Include:
   - Steps to reproduce the issue.
   - Proof-of-concept code or minimal reproduction environment.
   - Affected operating system and JVM version.

### Response Timeline
- **Initial Response:** Within 48 hours.
- **Triage & Status Update:** Within 5 business days.
- **Fix & Public Advisory:** Coordinated release within 14 days of confirmed reproduction.
