# Bunq2ynab

[![Docker Cloud Build Status](https://img.shields.io/docker/cloud/build/vjacobs/bunq2ynab.svg)](https://hub.docker.com/r/vjacobs/bunq2ynab)

Synchronizes outgoing Bunq transactions to YNAB.

## Usage

1. Add `#bunqimport` to the note of the account in YNAB you want to import transactions into
2. `docker run -e YNAB_API_KEY=<ynabApiKey> -e BUNQ_API_KEY=<bunqApiKey> vjacobs/bunq2ynab`
3. Every minute, new transactions are imported into YNAB
