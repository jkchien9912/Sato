const express = require('express');
const {Kafka, KafkaJSBrokerNotFound}=require('kafkajs');
const path = require('path');
const app = express();
const port = 4000;

var application_root = __dirname;
var Memcached = require('memcached');
var uuid = require('uuid'); 

app.use(express.urlencoded({extended: true}));
app.use(express.json());
app.use( express.static( path.join( application_root, '/') ) );

//Connect to Redpanda
const kafka = new Kafka({
  clientId: 'my-app',
  brokers: ['localhost:9092']
})

//Connect to memcached
const url = "localhost:11211"; 
var memcached = new Memcached();
memcached.connect(url, function(err, conn) {
  if(err) {
    console.log(conn.server,'error while connect to memcached'); 
  }
})

//This function tests the REST API
app.get('/api/hello', (req, res) => {
  res.send({'title':'Hello World!'})
})

//Delete the topics
app.get('/api/reset', async (req,res)=>{

  const admin = kafka.admin()
  await admin.connect();

  await admin.listTopics().then((topic_list)=>{
    console.log(topic_list);
     admin.deleteTopics({
      topics: topic_list
  }).then(()=>{ admin.disconnect();})

})})

app.get('/', function (req, res) {
  res.redirect('/index.html');
})

// Produce basic data for respanda
app.get('/api/produce', async (req, res) => {
  // Init Redpanda producer
  const producer = kafka.producer(); 
  await producer.connect(); 
  await producer.send({
    topic: 'my-app',
    messages: [
      { value: 'Hello Redpanda!' },
    ],
  })
  await producer.disconnect().then(()=>{ res.send({'status':'test message sent to my-app'});})
})

// Consume data fetched by redpanda
app.get('/api/consume', async (req, res) => {
  // Init Redpanda consumer
  const consumer = kafka.consumer({groupId: uuid.v4()}); 
  await consumer.connect();
  await consumer.subscribe({ topic: 'my-app' });

  consumer.run({
      eachMessage: async ({ topic, partition, message }) => {
        id=message.key.toString(); 
        // TODO: parse more complex data
        value=message.value.toString();

        const data = { id, value }
        console.log(data)
        memcached.set(id, JSON.stringify(data), 10000, function (err) {
          if(err) throw new err;
        });
        
        if (id=='STOP') { 
          console.log('\n\nStopping the data generation\n\n'); 
          consumer.disconnect(); 
          worker.terminate();
        }
      }, 
    }); 

})

// Testing on data stored in redpanda pipeline
app.get('/api/get', async (req, res, next) => {
  try {
    const id = req.query.id; 
    memcached.get(id, function (err, val) {
      if (err)
        throw err;
      if (val !== null) {
        console.log(val)
        return res.status(200).json(JSON.parse(val));
      } else {
        return next(); 
      }
    })
  } catch(err) {
    console.log("Did not give the correct format for accessing memcached"); 
    console.log(err); 
  }
})

// Testing on memcached
app.get('/api/memcached', async (req, res, next)=>{
  try {
    const requestType = req.query.type; 
    if (requestType == "set") {
      const body = req.query.body;
      const id = req.query.id; 
      const data = { id, body }
      memcached.set(id, JSON.stringify(data), 10000, function (err) {
        if(err) throw new err;
      });
      return res.status(201).json(data);
    } else if (requestType == "get") {
      const id = req.query.id; 
      memcached.get(id, function (err, val) {
          if (err)
            throw err;
          if (val !== null) {
            return res.status(200).json(JSON.parse(val));
          } else {
            return next(); 
          }
        })
    }
  } catch (err) {
    console.log("Did not give the correct format for accessing memcached"); 
    console.log(err); 
  }
})


//Main 
app.listen(port, () => {
  console.log(`Listening at http://localhost:${port}`)
})