#
# docker build -t heeler:latest .
# docker run -d --name heeler heeler:latest
# docker run -v /var/wombat:/mnt/wombat --name heeler heeler:latest
# docker run -v /var/wombat/fresh/heeler:/mnt/fresh -v /var/wombat/heeler:/mnt/output --name heeler heeler:latest 
#
FROM python:3.11-slim

WORKDIR /app

# Copy requirements if you have any, else skip
# COPY requirements.txt ./
# RUN pip install --no-cache-dir -r requirements.txt

# Create and activate virtualenv
RUN python -m venv /app/venv
ENV VIRTUAL_ENV=/app/venv
ENV PATH="$VIRTUAL_ENV/bin:$PATH"

COPY heeler_app.py ./
COPY converter.py ./

RUN mkdir -p /mnt/wombat
CMD ["python", "heeler_app.py"]
