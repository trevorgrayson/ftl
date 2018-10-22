entity_type: !map [tms_id, {EP.*: episode, SH.*: series, MV.*: movie}]

id: TMSId
tms_id: TMSId
root_id: rootId
fallback: nope|rootId
connector_id: connectorId
series_id: seriesId
season_id: seasonId
name: titles/title
description: descriptions/desc
premiere: origAirDate

titles:
    titles/title*:
        title: .
        language: lang
        size: size
        type: type

descriptions:
    descriptions/desc*:
        desc: .
        language: lang
        size: size
        type: type

genres:
    genres/genre*:
        name: .
        external_namespace: !lit gracenote
        external_identifier: genreId

duration: runTime
type: progType

ratings:
    ratings/rating*:
        code: code
        description: description
        authority: ratingsBody

releases:
    movieInfo/releases/release*:
        type: type
        date: date
        country: country


credits:
    cast/member*: &person
        characters: characterName*
        role: role
        first: name/first
        middle: name/middle
        last: name/last
        full_name: !template "$first $last"
        id: personId
        order: ord
    crew/member*:
        *person

artwork:
    assets/asset*:
        width: width
        height: height
        category: category
        type: type
        asset_id: assetId
        tier: tier
        action: action
        url: URI

language: origAudioLang
awards:
    awards/award*:
        name: name
        category: category
        year: year
        won: won
        external_namespace: !lit gracenote_award_category
        external_identifier: category/awardCatId

source: !lit gracenote

# TODO: get external_ids to work properly
external_ids:
    - /*:
        namespace: !lit tms
        id: TMSId
    - /*:
        namespace: !lit gracenote_episode
        id: rootId

season: episodeInfo/season


# TODO MAP ENUMS
