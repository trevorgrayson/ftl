id: TMSId|rootId
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

actors:
    cast/member*:
        role: role
        first: name/first
        last: name/last
        full_name: $first $last

# credits
# artwork
# language
# awards

# source:
# external_ids
# - namespace
# - id
# releases
# - datetime
# - release_type
# - country
# cast_and_crew - full_name
# - order
# - role
# - characters
# person_id

# TODO MAP ENUMS
