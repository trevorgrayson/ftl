id: TMSId|rootId
root: rootId
fallback: nope|rootId
name: !!str titles/title
description: descriptions/desc
premiere: origAirDate

ratings: ratings/rating*
genres: genres/genre*
duration: runTime
type: progType
ratings: ratings/rating/code*
releases:
    movieInfo/releases/release*:
        type: type
        date: date
        country: country

actors:
    cast/member*:
        role: role
        first: name/first
        last: name/last
        full_name: !template "$first $last"
        id: name/nameId

credits:
    crew/member*:
        role: role
        first: name/first
        last: name/last
        full_name: !template "$first $last"
        id: name/nameId

artwork:
    assets/asset*:
        width: width
        height: height
        category: category
        type: type
        asset_id: assetId
        tier: tier

language: origAudioLang
awards:
    awards/award*:
        name: name
        category: category
        year: year
        won: won

source: !lit gracenote

# external_ids
# - namespace
# - id

# TODO MAP ENUMS
