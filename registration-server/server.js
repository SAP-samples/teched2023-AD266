const express = require('express');
const swaggerUi = require('swagger-ui-express');
const swaggerDocument = require('./registration.json');
const cors = require('cors');

const app = express();
app.use(cors());
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
    sessionIDs: [101, 102],
  };
const sampleEvents = [
    techEd,
    { id: 2, name: 'Some other Event', sessionIDs: [] },
  ];

// Implement endpoints
app.get('/events', (req, res) => {
  res.json(sampleEvents);
});

app.get('/events/:eventId', (req, res) => {
  const eventId = req.params.eventId;
  const event = sampleEvents.find(event => event.id == eventId);

  if (!event) {
    res.status(404).json({ message: 'Event not found' });
  } else {
    res.json(event);
  }
});

app.post('/events/:eventId/register', (req, res) => {
  const eventId = req.params.eventId;
  const event = sampleEvents.find(event => event.id == eventId);

  if (!event) {
    res.status(404).json({ message: 'Event not found' });
  } else {
    res.status(201).json({ message: 'Sign up successful' });
  }
});

app.get('/events/:eventId/sessions', (req, res) => {
  const eventId = req.params.eventId;
  const event = sampleEvents.find(event => event.id == eventId);

  if (!event) {
    res.status(404).json({ message: 'Event not found' });
    return;
  }

  const sessionIDs = event.sessionIDs;
  const sessions = sessionIDs.map(sessionID => sampleSessions.find(session => session.id == sessionID));

  res.json(sessions);
});

app.post('/events/:eventId/sessions/:sessionId/register', (req, res) => {
  const eventId = req.params.eventId;
  const sessionId = req.params.sessionId;
  const event = sampleEvents.find(event => event.id == eventId);
  const session = sampleSessions.find(session => session.id == sessionId);
  if (!event || !session) {
    res.status(404).json({ message: 'Event or session not found' });
    return;
  }
  res.status(201).json({ message: 'Signed up for the session successfully' });
});

app.listen(port, () => {
  console.log(`Mock server is running on port ${port}`);
});
