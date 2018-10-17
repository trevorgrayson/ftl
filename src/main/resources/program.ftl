id: TMSId|rootId
root: rootId
series_id: connectorId|seriesId
fallback: nope|rootId
name: !!str titles/title
description: descriptions/desc
premiere: origAirDate

titles:
    titles/title*:
        title: .
        language: lang

descriptions:
    descriptions/desc*:
        desc: .
        language: lang

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
        character: characterName
        role: role
        first: name/first
        last: name/last
        full_name: !template "$first $last"
        id: name/nameId
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

external_ids:
    /TMSId*:
        namespace: !lit tms
        id: .
    /rootId*:
        namespace: !lit gracenote_episode
        id: .

season: episodeInfo/season


# TODO MAP ENUMS
