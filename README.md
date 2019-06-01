# Bunq2ynab

[![Docker Cloud Build Status](https://img.shields.io/docker/cloud/build/vjacobs/bunq2ynab.svg)](https://hub.docker.com/r/vjacobs/bunq2ynab)

Synchronizes outgoing Bunq transactions to YNAB.

## Usage

1. `docker run -P -e YNAB_API_KEY=<ynabApiKey> -e BUNQ_API_KEY=<bunqApiKey> vjacobs/bunq2ynab`
2. All outgoing transactions in all Bunq bank accounts will be imported into the first account that has `#bunqimport` in the note, from the last used budget
