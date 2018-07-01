const express = require('express');
const cors = require('cors');
const bodyParser = require('body-parser');
const http = require('http');
const https = require('https');
const pg = require('pg');
const path = require('path');


const dbUser = process.env.ADMIN_DB_USER;
const dbPass = process.env.ADMIN_DB_PASS;
const dbName = 'cafe';
const connectionString = process.env.CAFE_DATABASE_URL || `postgres://${dbUser}:${dbPass}@localhost:5432/${dbName}`;
console.log('connectionString', connectionString);

const pool = new pg.Pool({
    connectionString: connectionString
});


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


// DB Connection and Server start //

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
