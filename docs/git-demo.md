# Git Demo Commands

Remote repository:

```text
https://github.com/HeChiMoon/Distributed-Medical-Information-System.git
```

Baseline commit demo:

```powershell
git status --short --branch
git add .
git commit -m "init: scaffold distributed medical information system"
git remote -v
git push -u origin main
```

History demo:

```powershell
git log --oneline --decorate -5
```

Current local milestone commits include:

```text
10d5902 feat(core): complete milestone 2 business services
```

Milestone 3 push demo:

```powershell
git status --short --branch
git remote add origin https://github.com/HeChiMoon/Distributed-Medical-Information-System.git
git remote -v
git push -u origin main
```

If `origin` already exists:

```powershell
git remote set-url origin https://github.com/HeChiMoon/Distributed-Medical-Information-System.git
git push -u origin main
```
