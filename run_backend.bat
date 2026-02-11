@echo off
cd backend
call backend_venv\Scripts\activate
python -m app.main
pause
