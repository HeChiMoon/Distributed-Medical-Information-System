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
cc8da61 feat(demo): complete milestone 3 integration showcase
10d5902 feat(core): complete milestone 2 business services
```

Milestone 3 push demo:

```powershell
git status --short --branch
git remote add origin https://github.com/HeChiMoon/Distributed-Medical-Information-System.git
git remote -v
git push -u origin main
```

Milestone 4 local push attempt result:

```text
git push -u origin main
fatal: unable to access 'https://github.com/HeChiMoon/Distributed-Medical-Information-System.git/': schannel: failed to receive handshake, SSL/TLS connection failed

git -c http.sslBackend=openssl push -u origin main
fatal: unable to access 'https://github.com/HeChiMoon/Distributed-Medical-Information-System.git/': TLS connect error: error:0A000126:SSL routines::unexpected eof while reading
```

The repository is committed locally. After the TLS/network issue is fixed, run:

```powershell
git push -u origin main
```

If `origin` already exists:

```powershell
git remote set-url origin https://github.com/HeChiMoon/Distributed-Medical-Information-System.git
git push -u origin main
```
