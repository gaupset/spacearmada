import {setGlobalOptions} from "firebase-functions";
import {onRequest} from "firebase-functions/https";
import * as logger from "firebase-functions/logger";
import {initializeApp} from "firebase-admin/app";
import {handleGameRequest} from "./gameRunner";

const databaseURL = process.env.DATABASE_URL ||
  "https://invaders99-3f807-default-rtdb.europe-west1.firebasedatabase.app";
logger.info("initializeApp", {databaseURL});
initializeApp({databaseURL});
setGlobalOptions({maxInstances: 10});

export const helloWorld = onRequest((request, response) => {
  logger.info("Hello logs!", {structuredData: true});
  response.send("Hello from Firebase!");
});

export const gameHandler = onRequest(async (request, response) => {
  if (request.method !== "POST") {
    response.status(405).json({status: "error", message: "Method not allowed"});
    return;
  }

  logger.info("gameHandler called", {
    method: request.method,
    contentType: request.headers["content-type"],
    bodyType: typeof request.body,
    body: request.body,
  });

  const {lobbyId, lobbyUserId, action} = request.body || {};

  if (!lobbyId || !lobbyUserId) {
    response.status(400).json({
      status: "error",
      message: "Missing lobbyId or lobbyUserId",
    });
    return;
  }

  try {
    const result = await handleGameRequest(lobbyId, lobbyUserId, action);
    response.json(result);
  } catch (error) {
    logger.error("gameHandler error", {error, lobbyId, lobbyUserId});
    response.status(500).json({status: "error", message: "Internal error"});
  }
});
