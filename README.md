# Trading Discipline Incharge

Android MVP for trading behaviour discipline and workflow control.

## Goal

This is not a trading journal replacement and not a signal app. Its job is to act as a personal trading discipline gate:

- before market: confirm plan, max loss, max trades, allowed instrument
- during market: fast 4-input trade permission check
- after market: review process, mistake tags, and lessons

## Mode logic

The app uses actual IST through `ZoneId.of("Asia/Kolkata")`.

Weekdays:

- 05:00–09:14 IST: Before Market
- 09:15–15:30 IST: Live Trading
- 15:31–23:59 IST: Post Market
- 00:00–04:59 IST: Rest Mode

Saturday/Sunday:

- Weekend Closed

No manual testing override is included in this MVP because the final behaviour should be time-linked.

## Live trade permission inputs

Only four fields are used during trading:

1. Emotional condition — dropdown
2. Current day P&L — number input
3. Trades already taken — dropdown
4. Why am I taking this trade? — dropdown

## Current rule behaviour

Immediate block:

- morning plan not completed
- daily max loss reached
- max trade count reached
- revenge feeling
- recovering loss
- weak trade reason: “not sure” or “just felt like entering”
- morning condition marked as not fit to trade

Wait:

- FOMO / chasing feeling
- chasing fast move

Caution:

- frustration after loss
- overconfidence
- more than 60% daily loss used
- near max trade count
- emotional morning state + emotional live state

Allowed:

- plan complete, risk within limits, valid reason, acceptable emotional state

## Tech stack

- Kotlin
- Jetpack Compose
- Room local database
- Material 3 UI

## Open in Android Studio

1. Open Android Studio.
2. Choose `Open`.
3. Select this folder: `TradingDisciplineIncharge`.
4. Let Gradle sync.
5. Run the `app` configuration on an emulator or phone.

## Important next steps

- Add proper discipline score calculation.
- Add calendar / heatmap.
- Add screenshot attachment for one marked chart image.
- Add weekly behavioural analytics.
- Add export/backup.
- Add NSE holiday calendar later.
