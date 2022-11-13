const express = require('express');
const {Kafka, KafkaJSBrokerNotFound}=require('kafkajs');
const memjs = require('memjs'); 
const path = require('path');
var fs = require('fs');
const app = express();
const port = 4000;

var application_root = __dirname;
// var Memcached = require('memcached');

app.use(express.urlencoded({extended: true}));
app.use(express.json());
app.use( express.static( path.join( application_root, '/') ) );

//Connect to Redpanda
const kafka = new Kafka({
  clientId: 'my-app',
  brokers: ['redpanda:9092']
})

//Connect to memcached
const url = "localhost:11211"; 
const memcached = memjs.Client.create(url); 
// var memcached = new Memcached(url);

//This function tests the REST API
app.get('/api/hello', (req, res) => {
  res.send({'title':'Hello World!'})
})

//This function tests the connectivity to Redpanda
app.get('/api/test', async (req,res)=>
{
  const producer = kafka.producer()

  console.log('connecting...');
  await producer.connect()

  console.log('sending...');
  await producer.send({
    topic: 'test-topic',
    messages: [
      { value: 'Hello Redpanda!' },
    ],
  })
  
  await producer.disconnect().then(()=>{ res.send({'status':'test message sent to test-topic'});})
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

app.get('/api/memcached', async (req, res)=>{
  try {
    const requestType = req.query.type; 
    const id = req.query.id; 
    if (requestType == "set") {
      const body = req.query.body;
      try {
        const data = { id, body };
        await memcached.set(id, JSON.stringify(data), { expires: 12 });
        return res.status(201).json(data);
      } catch (err) {
        console.log("Error memcached");
        console.log(err);
      }
    } else if (requestType == "get") {
      memcached.get(id, (err, val) => {
        if (err) throw err; 
        if (val !== null) {
          return res.status(200).json(JSON.parse(val)); 
        }
      })
    }
  } catch (err) {
    console.log("Did not giving the correct format for accessing memcached"); 
    console.log(err); 
  }
})

//Main 
app.listen(port, () => {
  console.log(`Listening at http://localhost:${port}`)
})