FROM python:3.5-slim
WORKDIR /app
ADD github-events-source.py /app
ADD run.properties /app
ADD requirements.txt /app
RUN pip install -r requirements.txt
EXPOSE 80
CMD ["python", "github-events-source.py"]
