# tg-news-bot
<h2>Description</h2>
This is simple telegram bot, witch parse news from the list of journals (RSS feed), save it to the database, and everyday, 
at 08:00, 14:20, 20:00 send short feed to the subscribers.

<h3>To try</h3>
To try this bot you just need to send message to @newswtcnsbot in <a href = "https://web.telegram.org/">telegram</a>, which will automatically subscribe you on brief news mailing.

<h3>To use</h3>
To use this bot, you need to register your own bot with (@BotFather in <a href = "https://web.telegram.org/">telegram</a>), then add to your system environment two variables:
<ul>
  <li>BOT_TOKEN - with your bot's token</li>
  <li>BOT_NAME - with your bot's name</li>
  <li>DATABASE_URL - with your database URI. This database must include two tables: 
    <ul>
      <li>subscribers - with columns: chatID (int8) and settings (int4)</li>
      <li>news - with columns: link (varchar), publisheddate (timestamp), resource (varchar), title (varchar)</li>
    </ul>
</ul>
SSL required!

<h2>Bot commands:</h2> 
/news [parsePeriod] - to get instant news for the period (in hours, 12 by default)
/help - for help

<h2>Used techs:</h2>
<ul>
  <li>Maven</li>
  <li>telegramBotsApi</li>
  <li>RomeTools</li>
  <li>PostgreSQL</li>
  <li>JDBC</li>
  <li>Java Collections</li>
</ul>

