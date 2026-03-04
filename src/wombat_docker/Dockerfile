#
# docker build -t heeler:latest .
# docker run -v /var/wombat:/mnt/wombat --name heeler heeler:latest
#
FROM python:3.11-slim

WORKDIR /app


# Create and activate virtualenv first
RUN python -m venv /app/venv
ENV VIRTUAL_ENV=/app/venv
ENV PATH="$VIRTUAL_ENV/bin:$PATH"

# Copy requirements and install them inside the venv
COPY requirements.txt ./
RUN $VIRTUAL_ENV/bin/pip install --no-cache-dir -r requirements.txt

COPY file_helper.py ./
COPY heeler_app.py ./
COPY postgres.py ./
COPY sql_table.py ./

RUN mkdir -p /mnt/wombat
CMD ["python", "heeler_app.py"]
