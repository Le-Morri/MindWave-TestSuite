#MindWave-TestSuite

A small test tool for the Neurosky MindWave Mobile

#What to do?

You can easily adapt the tests if you manipulate the json files in the config or dist/config folder.

##test.json
This file describes the tests. Therefore it has an array of objects.

Each object has two mandatory fields. `type` and `label`. Optional there is the `image` field.

The `type` can eighter be `test` or `showText`. The `label` is the localized description. It must be present in the locale file.
And the `image` property ist the filename of the image in the `asstes` folder.

##config.json

Presents some configurations for the programm.

##locale_de.json

Here you find the translations for each node. 