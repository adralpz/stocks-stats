import { Logger } from "./logger";

const response = await Bun.file("response.txt").text();
const stocksText = await Bun.file("stocks.txt").text();
const stocks = stocksText.split(",");

type comment = {
  body: string;
  stock: string;
}

const comments: comment[] = [];

for (const [i, body] of response.split("----------------").entries()) {
  let contains = false;
  let stock = "";
  for (const s of stocks) {
    if (body.includes(`\$${s}`) || body.includes(" " + s + " ")) {
      console.log("The body includes the stock " + s);
      stock = s;
      break;
    }
  }

  console.log("Body", body)

  // if (!contains) {
  //   comments.push({
  //     body: body,
  //     stock: stock,
  //   });

  //   continue;
  // }

  const options = {
    method: 'POST',
    headers: { 'Content-Type': 'application/json' },
    body: JSON.stringify({
      model: 'llama3:latest',
      format: "json",
      options: { "temperature": 0.8 },
      stream: false,
      messages: [
        {
          role: 'system',
          content: `
        You are a stock market professional and from the input which are a comment
        from a stock market forum, you will reply only with the currency symbol of
        the stock mentioned in the comment.

        If there are multiple currencies, separate them with a comma without spaces.

        Example:
        
        Input: 
        "Oh forgot to mention, I’m still holding 4x AAPL 6/28 215c’s 😂"
        
        Output: 
        "AAPL"

        Input:
        "I bought 127.5 puts this morning 0dte. Spy puts too, I was sure some sort of end of the week sell off would happen but NOPE"
        
        Output:
        "SPY"

        Input:
        "Hello guys!"

        Output:
        "None"
        `,
        },
        {
          role: 'user',
          content: body,
        },
      ],
    }),
  };

  // repeat infinitely the fetch until the response is in stocks.txt
  let i = 0;
  while (true) {
    const response = await fetch('http://localhost:11434/v1/chat/completions', options);
    const json = await response.json();

    const stockResponse = json.choices[0].message.content;

    if (stockResponse.toLowerCase().includes("none")) {
      console.log("stock not found");
      break;
    }

    if (stocks.includes(stockResponse)) {
      comments.push({
        body: body,
        stock: stockResponse,
      });

      console.log(stockResponse + " found");
      break;
    } else {
      // console.log(stockResponse + " not found trying again...");
      Logger.warn(`Stock not found in the message: ${stockResponse}`);
    }

    i++;
    if (i > 100) {
      break;
    }
  }
}