# Card

> Cards are surfaces that groups content and actions in the same space. Its construction must always create a visual hierarchy of the content and communicate its purposes.
> Extends from [MaterialCardView](https://developer.android.com/reference/com/google/android/material/card/MaterialCardView).

## Properties:

| Property           | Values                         | Status            |
| --------------     | -------------------------      | ----------------- |
| Elavation             | True, False                           | ✅  Available     |
| Radius         | True, False    | ✅  Available     |

<br>

## Technical Usages Examples

##### Card with radius and elevation enabled

![Card](./images/card_allEnabled.png)

```android
    <com.natura.android.card.Card
        android:id="@+id/card"
        android:layout_width="match_parent"
        android:layout_height="wrap_content"
        app:enabledRadius="true"
        app:enabledElevation="true">
    </Card>
```

<br><br>

##### Card with radius disabled

![Card](./images/card_radiusDisabled.png)

```android
    <com.natura.android.card.Card
        android:id="@+id/card"
        android:layout_width="match_parent"
        android:layout_height="wrap_content"
        app:enabledRadius="false"
        app:enabledElevation="true">
    </Card>
```

<br><br>

##### Card with elevation disabled

![Card](./images/card_elevationDisabled.png)

```android
    <com.natura.android.card.Card
        android:id="@+id/card"
        android:layout_width="match_parent"
        android:layout_height="wrap_content"
        app:enabledRadius="true"
        app:enabledElevation="false">
    </Card>
```

<br><br>

## More code
You can check out more examples from SampleApp by clicking [here](https://github.com/natura-cosmeticos/natds-android/tree/master/sample/src/main/res/layout/activity_card.xml).
