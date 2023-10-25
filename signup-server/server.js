const express = require('express');
const swaggerUi = require('swagger-ui-express');
const swaggerDocument = require('./signup.json');

const app = express();
const port = process.env.PORT || 5000;

// Serve the Swagger UI
app.use('/api-docs', swaggerUi.serve, swaggerUi.setup(swaggerDocument));

const sampleSessions = [
    { id: 101, title: 'Session A', duration: 60, location: 'Location A' },
    { id: 102, title: 'Session B', duration: 90, location: 'Location B' },
  ];
const techEd = {
    id: 1,
    name: 'TechEd 2023',
    sessions: sampleSessions,
  };
const sampleEvents = [
    techEd,
    { id: 2, name: 'Some other Event' }
  ];

// Implement endpoints
app.get('/events', (req, res) => {
  res.json(sampleEvents);
});

app.get('/events/:eventId', (req, res) => {
  const eventId = req.params.eventId;

  res.json(sampleEvents.find(event => event.id == eventId));
});

app.post('/events/:eventId/register', (req, res) => {
  res.status(201).json({ message: 'Sign up successful' });
});

app.get('/events/:eventId/sessions', (req, res) => {
  const eventId = req.params.eventId;
  sampleEvents.find(event => event.id == eventId).sessions;
  res.json(sampleSessions);
});

app.post('/events/:eventId/sessions/:sessionId/register', (req, res) => {
  res.status(201).json({ message: 'Signed up for the session successfully' });
});

app.listen(port, () => {
  console.log(`Mock server is running on port ${port}`);
});
