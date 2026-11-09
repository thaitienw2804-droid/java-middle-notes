# Tuần 12 (25/01 – 31/01/2027): CI/CD Pipeline + Hardening project

> Skill audit: CI/CD = 3 (đã dùng Jenkins/GitLab ở FPT) → mục tiêu 5. Tuần này biến kinh nghiệm "dùng pipeline người khác dựng" thành "tự thiết kế pipeline và kể được vì sao".

---

## Ngày 78 — Thứ 2 (25/01): Khái niệm CI/CD + Git workflow
**Kiến thức phải nắm:**
- CI (tích hợp liên tục: build + test mỗi commit) vs Continuous Delivery (luôn sẵn sàng deploy, prod cần approve) vs Continuous Deployment (tự lên prod).
- Pipeline = chuỗi kiểm chứng: checkout → build → unit test → integration test → static analysis/lint → security scan → build image → push registry → deploy staging → smoke test → approve → deploy prod → giám sát → rollback.
- Git workflow: **trunk-based** vs GitFlow vs GitHub Flow; branch protection, PR bắt buộc CI xanh + review; merge vs rebase vs squash; conventional commits; **semantic versioning**; tag image theo **commit SHA** (vì sao `latest` nguy hiểm).
- Git nâng cao hay hỏi: `rebase -i`, `cherry-pick`, `revert` vs `reset`, giải quyết conflict, `git bisect`.

**Code tay:** chuẩn hóa repo: branch protection `main`, PR template, CODEOWNERS (tùy), commit convention; thực hành `rebase -i`, `revert`, `bisect` trên repo nháp.

**Tự kiểm tra:** Delivery vs Deployment? Trunk-based vs GitFlow? `revert` vs `reset`? Vì sao không tag `latest`?

---

## Ngày 79 — Thứ 3 (26/01): GitHub Actions (hoặc GitLab CI) — pipeline CI
**Kiến thức phải nắm:**
- GitHub Actions: workflow, trigger (`pull_request`, `push`, `workflow_dispatch`), job, step, runner, `needs`, matrix, artifact, **cache Maven** (`actions/setup-java` cache), `paths` filter.
- GitLab CI tương đương: stages, jobs, `rules`, `needs`, cache, artifacts, runner, environment.
- Chất lượng: JaCoCo coverage gate, SonarCloud/Checkstyle/Spotless; **fail fast** (lint/unit trước, integration sau).
- Testcontainers trên CI (runner có Docker).

**Code tay:** `.github/workflows/ci.yml`: PR → setup JDK 21 + cache → `mvn verify` (unit + Testcontainers) → JaCoCo report → Sonar (tùy chọn). Monorepo: job theo từng service với `paths` filter / `dorny/paths-filter`.

**Tự kiểm tra:** Kể các stage và stage nào fail thì dừng. Monorepo chỉ build service thay đổi thế nào? Pipeline chậm tối ưu từ đâu?

**LeetCode (20'):** Permutations, Combination Sum.

---

## Ngày 80 — Thứ 4 (27/01): Build image, registry, secrets, security scan
**Kiến thức phải nắm:**
- Build & push image lên GHCR/Docker Hub/GitLab Registry, tag `sha-<short>` + version.
- **Secrets**: GitHub Secrets/Environments, GitLab masked/protected variables; không echo secret; OIDC thay key dài hạn (biết khái niệm); secret đã lỡ commit → **rotate ngay**, xóa khỏi lịch sử chưa đủ.
- Supply chain: dependency scan (Dependabot, OWASP dependency-check), image scan (**Trivy**), secret scan (gitleaks).
- Environment staging/production, cấu hình qua env var (12-factor app).

**Code tay:** job `build-image` chỉ chạy trên `main`: build → Trivy scan (fail nếu CRITICAL) → push GHCR; bật Dependabot.

**Tự kiểm tra:** Quản lý secret thế nào? Secret lỡ push lên GitHub xử lý ra sao? 12-factor app gồm gì nổi bật?

---

## Ngày 81 — Thứ 5 (28/01): CD — deploy staging/production + rollback
**Kiến thức phải nắm:**
- Deploy lên VPS qua SSH (`docker compose pull && up -d`) hoặc lên K8s (`kubectl set image`/Helm/ArgoCD — **GitOps** biết khái niệm).
- Staging tự động; production cần **manual approval** (GitHub Environments required reviewers / GitLab `when: manual`) — vì sao.
- Smoke test sau deploy (gọi `/actuator/health`, 1 API chính).
- **Rollback**: deploy lại image tag cũ (nhanh nhất), `kubectl rollout undo`; DB migration không rollback dễ → expand/contract.
- Flyway chạy ở đâu: lúc app khởi động vs job riêng trước deploy — trade-off.
- **Jenkins** (kinh nghiệm FPT): Jenkinsfile declarative (`pipeline`, `agent`, `stages`, `post`), credentials binding, shared library; so sánh với GitHub Actions/GitLab CI.

**Code tay:** `cd.yml`: merge `main` → deploy staging tự động → smoke test → job `deploy-prod` cần approve → script `rollback.sh <tag>`; đo thời gian rollback (< 5 phút).
- **Đóng gói câu chuyện FPT #3 (30')**: Viết 1 trang note so sánh **Pipeline Jenkins / GitLab CI ở dự án FPT của bạn** với pipeline GitHub Actions của `shop-order-platform`: Ở FPT có những stage nào? Quản lý secret/environment ra sao? Khi deploy lỗi ở FPT thì quy trình rollback thực tế diễn ra thế nào? Điểm nào ở FPT chưa tối ưu mà bạn đã cải tiến trong project cá nhân?

**Tự kiểm tra:** Kể pipeline của bạn từ PR đến production. Rollback nhanh nhất là gì, mất bao lâu? Jenkins vs GitHub Actions vs GitLab CI?

---

## Ngày 82 — Thứ 6 (29/01): Hardening bảo mật + chất lượng
**Checklist rà soát (làm và ghi lại):**
- Secret: không còn trong code/compose (dùng `.env` không commit + secrets CI), quét bằng gitleaks.
- API: validation đầy đủ, rate limit, CORS chỉ origin cho phép, actuator không public, error không lộ stack trace, log không in token/password.
- Dependency & image: không còn CVE CRITICAL.
- Graceful shutdown, timeout mọi lời gọi ngoài, circuit breaker, connection pool hợp lý.
- Test: coverage service layer ≥ 70%, integration test luồng đặt hàng.
- OWASP Top 10 rà lần cuối (IDOR, injection, SSRF nếu có gọi URL do user nhập).

**LeetCode (20'):** Trapping Rain Water (hard nhẹ), Sliding Window Maximum.

---

## Ngày 83 — Thứ 7 (30/01): Lab 4–5h — pipeline xanh end-to-end + README
- Chạy trọn: tạo PR → CI xanh → merge → staging tự deploy → approve → prod → cố ý deploy bản lỗi → alert Telegram → rollback → ghi incident note.
- Chụp ảnh: pipeline từng stage, Grafana, alert Telegram, Uptime Kuma, Zipkin trace.
- **README** shop-order-platform: giới thiệu, sơ đồ kiến trúc, tech stack + lý do (link ADR), chạy local 5 phút, luồng Saga, số liệu performance, bảo mật, observability, CI/CD, "lỗi từng gặp & cách fix", roadmap.

**LeetCode (30'):** Ôn lại 5 bài sai nhiều nhất từ tuần 1–11.

---

## Ngày 84 — Chủ nhật (31/01): Review + luyện miệng + chấm skill cuối
- **Chấm lại toàn bộ `skill-audit.md`** — kỹ năng nào < 4 thì ghi vào kế hoạch sau Tết.

**12 câu phỏng vấn của tuần (khớp ngân hàng CI/CD trong lộ trình):**
1. Kể toàn bộ pipeline project của bạn từ PR đến production.
2. Stage nào fail thì dừng, stage nào chỉ cảnh báo?
3. Quản lý secret; lỡ commit secret thì sao?
4. Staging tự động, production approve tay — vì sao?
5. Tag image bằng gì, vì sao không `latest`?
6. Pipeline chậm thì tối ưu từ đâu?
7. Test fail trên CI nhưng pass local — debug thế nào?
8. Rollback nhanh nhất mất bao lâu?
9. Jenkins vs GitHub Actions vs GitLab CI.
10. Migration DB trong pipeline ở bước nào, rollback migration thế nào?
11. Monorepo chỉ build service thay đổi?
12. Làm sao biết deploy mới có lỗi, ai quyết rollback?

**Checklist:** ☐ Pipeline xanh + bằng chứng rollback ☐ README hoàn chỉnh ☐ Bộ ảnh demo ☐ skill-audit cuối

