export function getContent(message) {
  return JSON.parse(message.body).content;
}
