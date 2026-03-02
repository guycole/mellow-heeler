# Heeler Python App

Verify heeler collection 

## Files
- heeler_app.py: Main application
- Dockerfile: Container build instructions

## Build and Run

1. Build the Docker image:

   docker build -t heeler .

2. Run the container:

   docker run --rm heeler

The app will log a message every 15 seconds.
