Towny Info plugin
=======

Additional features for the Towny plugin such as: information signs about cities and nations, leading nation.

### Softdepends:
- PlaceholderAPI
- DiscordSRV
- BetonQuest

Information signs:
=======
In `town-sign-lines` and `nation-sign-lines` you can set info
which will be displayed in signs from
`town-signs` and `nation-signs`.

It can have any number of signs vertically.

Leader nation
=======
If this option is set:
`nation-status-update-time: '18:00'`
then in this time status of leader nation will be updated and
nation which have the largest count of residents in controlled
cities become to leader nation.

In this section you can specify reward for capture or holding this status:
```
nation-rewards:
  money: 1000
  main-points: 5
```

Additional functionality for BetonQuest
=======
### Conditions:
- isnationmain
- isnationadmin
- istownadmin
- isnationhasmainpoints <points>
### Events:
- changenationpoints <points>

Placeholders
=======
`%ti_nationismain%` - is player nation is main.
`%ti_nationmainpoints%` - count of main nation points.

Screenshots
=======
<div align="center"><img src="https://github.com/honnisha/towny-info/blob/main/screenshots/1.jpg?raw=true"/></div>
<div align="center"><img src="https://github.com/honnisha/towny-info/blob/main/screenshots/2.jpg?raw=true"/></div>
