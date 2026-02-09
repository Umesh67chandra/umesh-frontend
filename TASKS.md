# Focus Guardian Task Sheet

## Status Legend
- [ ] Pending
- [x] Done

## Immediate UI Fixes
- [x] Splash screen: remove emoji subtitle
- [x] Splash screen: adjust logo scaling to avoid side clipping
- [x] Login screen: remove emoji subtitle
- [x] Login screen: adjust logo scaling to avoid side clipping
- [x] Create account screen: remove emoji subtitle
- [x] Create account screen: adjust logo scaling to avoid side clipping

## Backend & Integration (Planned)
- [x] Supabase client + repository layer
- [x] Create account: insert user in Supabase
- [x] Login: verify credentials via Supabase
- [x] Persist role and onboarding preferences
- [ ] Create user_preferences table + RLS policy in Supabase
- [ ] Define remaining API contract (usage, alerts, settings)
- [ ] Implement Flask backend skeleton (usage, alerts, settings)
- [ ] Set up database models and migrations (non-auth data)

## Accessibility & Guardian Mode
- [ ] AccessibilityService events: app usage detection
- [ ] Content filtering rules (blocklist + heuristics)
- [ ] Guardian Mode toggles + permission UX

## Screen-by-Screen Implementation (Pending Review)
- [ ] Splash
- [ ] Login
- [ ] Create Account
- [ ] Role Selection
- [ ] Interest Selection
- [ ] Refine Interest
- [ ] Dashboard
- [ ] Notifications
- [ ] Addiction Score
- [ ] Sleep Cycle
- [ ] Challenges
- [ ] Analytics
- [ ] Smart Wallpaper
- [ ] Profile Settings
- [ ] Manage Apps

## Deployment
- [ ] Backend deployment target selection
- [ ] Environment variables + secrets
- [ ] Release build + signing
- [ ] Store listing assets
