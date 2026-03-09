# Shop Block

Shop Block prevents accidentally selling valuable items to NPC shops.

## Features

- Blocks sell actions when an item's value is at or above a configured threshold
- Supports two value sources:
  - Grand Exchange price
  - High Alchemy value
- Optional chat warning when a sell action is blocked

## Configuration

- **Value Threshold**: minimum value (in GP) that triggers blocking
- **Price Type**: GE Price or HA Value
- **Show Warning**: show a chat message for blocked sells

## Development

Run from the plugin root:

```powershell
.\gradlew.bat run
```