const express = require('express');
const cors = require('cors');
const bodyParser = require('body-parser');
const pg = require('pg');

const dbUser = process.env.ADMIN_DB_USER;
const dbPass = process.env.ADMIN_DB_PASS;
const dbName = 'cafe';
const connectionString = process.env.CAFE_DATABASE_URL || `postgres://${dbUser}:${dbPass}@localhost:5432/${dbName}`;
console.log('connectionString', connectionString);

const pool = new pg.Pool({
    connectionString: connectionString
});

const app = express();
app.use(bodyParser.json());
app.use(bodyParser.urlencoded({extended: true}));

// TODO: use reduced cors in production.
// const whitelist = ['https://cafelink.com', 'https://www.cafelink.com'];
// app.use(cors({ origin: whitelist }));

app.use(cors());

pool.on('error', (err, client) => {
    console.error('Unexpected error on idle client', err)
    process.exit(-1)
});

app.get('/api/hello', (req, res) => {
    return res.json("hello world");
});

app.get('/api/info/user/:userId', (req, res) => {
    const userId = req.params.userId;
    pool.query(query, (err, result) => {
        if (err) {
            console.error('postVote error', err);
            return res.status(500).json(err);
        }

        const rows = result.rows;
        return res.json(rows.length > 0);
    });
});

app.get('/api/conversations/user/:userId', (req, res) => {
    const userId = req.params.userId;
    pool.query(query, (err, result) => {
        if (err) {
            console.error('postVote error', err);
            return res.status(500).json(err);
        }

        const rows = result.rows;
        return res.json(rows.length > 0);
    });
});

app.get('/api/conversations/cafe/:cafeId', (req, res) => {
    const cafeId = req.params.cafeId;
    pool.query(query, (err, result) => {
        if (err) {
            console.error('postVote error', err);
            return res.status(500).json(err);
        }

        console.error('postVote success', checkVoteQuery, result);
        const rows = result.rows;
        return res.json(rows.length > 0);
    });
});


app.post('/api/message', (req, res) => {
    const body = req.body;
    // const query = `INSERT` ...
    pool.query(query, (err, result) => {
        console.log('issues', err, result);
        if (err) {
            console.error('issues', err);
            return res.status(500).json(err)
        }
        // Return the rows that lie within the bounds of the map view.
        return res.json(result.rows);
    });
});

/******/
/* DB Connection and Server start */
/******/

pool.connect((err, client, done) => {
    if (err) {
        console.error('postgres connection error', err);
        if (requirePostgres) {
            console.error('exiting');
            return;
        }
        console.error('continuing with disabled postgres db');
    }

    server.listen(PORT, () => {
        console.log('Express server listening on localhost port: ' + PORT);
    });
});
