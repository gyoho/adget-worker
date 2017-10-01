# adget-worker

## Problem Statement
1. SDK requests an ad from the ad server by caterogy
2. Ad server needs to return an ad with highest price for the requested category
3. Advertisers update their ads inventory regulary
4. Ad server needs adopt this update too

```
Ad
 |-- adId: String
 |-- category: String
 |-- price: Double
 |-- url: URL
```


## TODO
 - Don't do batch update and wait until all advertiser returns or timeout
 - Some advertiser update its inventory frequently and some don't
 - Update individually with timeout and retry if an advertiser timeouts
 
