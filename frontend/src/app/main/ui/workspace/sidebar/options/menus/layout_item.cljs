;; This Source Code Form is subject to the terms of the Mozilla Public
;; License, v. 2.0. If a copy of the MPL was not distributed with this
;; file, You can obtain one at http://mozilla.org/MPL/2.0/.
;;
;; Copyright (c) KALEIDOS INC

(ns app.main.ui.workspace.sidebar.options.menus.layout-item
  (:require-macros [app.main.style :as stl])
  (:require
   [app.common.data :as d]
   [app.common.types.shape.layout :as ctl]
   [app.main.data.workspace :as udw]
   [app.main.data.workspace.shape-layout :as dwsl]
   [app.main.refs :as refs]
   [app.main.store :as st]
   [app.main.ui.components.numeric-input :refer [numeric-input*]]
   [app.main.ui.components.radio-buttons :refer [radio-button radio-buttons]]
   [app.main.ui.components.title-bar :refer [title-bar]]
   [app.main.ui.icons :as i]
   [app.main.ui.workspace.sidebar.options.menus.layout-container :refer [get-layout-flex-icon]]
   [app.util.dom :as dom]
   [app.util.i18n :as i18n :refer [tr]]
   [rumext.v2 :as mf]))

(def layout-item-attrs
  [:layout-item-margin      ;; {:m1 0 :m2 0 :m3 0 :m4 0}
   :layout-item-margin-type ;; :simple :multiple
   :layout-item-h-sizing    ;; :fill :fix :auto
   :layout-item-v-sizing    ;; :fill :fix :auto
   :layout-item-max-h       ;; num
   :layout-item-min-h       ;; num
   :layout-item-max-w       ;; num
   :layout-item-min-w       ;; num
   :layout-item-align-self  ;; :start :end :center :stretch :baseline
   :layout-item-absolute
   :layout-item-z-index])

(defn- select-margins
  [m1? m2? m3? m4?]
  (st/emit! (udw/set-margins-selected {:m1 m1? :m2 m2? :m3 m3? :m4 m4?})))

(defn- select-margin
  [prop]
  (select-margins (= prop :m1) (= prop :m2) (= prop :m3) (= prop :m4)))

(mf/defc margin-simple
  {::mf/props :obj}
  [{:keys [values on-change on-blur]}]
  (let [m1 (:m1 values)
        m2 (:m2 values)
        m3 (:m3 values)
        m4 (:m4 values)

        m1 (when (and (not= values :multiple) (= m1 m3)) m1)
        m2 (when (and (not= values :multiple) (= m2 m4)) m2)

        on-focus
        (mf/use-fn
         (fn [event]
           (let [attr (-> (dom/get-current-target event)
                          (dom/get-data "name")
                          (keyword))]
             (case attr
               :m1 (select-margins true false true false)
               :m2 (select-margins false true false true))

             (dom/select-target event))))

        on-change'
        (mf/use-fn
         (mf/deps on-change)
         (fn [value event]
           (let [attr (-> (dom/get-current-target event)
                          (dom/get-data "name")
                          (keyword))]
             (on-change :simple attr value))))]


    [:div {:class (stl/css :margin-simple)}
     [:div {:class (stl/css :vertical-margin)
            :title "Vertical margin"}
      [:span {:class (stl/css :icon)}
       i/margin-top-bottom-refactor]
      [:> numeric-input* {:class (stl/css :numeric-input)
                          :placeholder "--"
                          :data-name "m1"
                          :on-focus on-focus
                          :on-change on-change'
                          :on-blur on-blur
                          :nillable true
                          :value m1}]]

     [:div {:class (stl/css :horizontal-margin)
            :title "Horizontal margin"}
      [:span {:class (stl/css :icon)}
       i/margin-left-right-refactor]
      [:> numeric-input* {:class (stl/css :numeric-input)
                          :placeholder "--"
                          :data-name "m2"
                          :on-focus on-focus
                          :on-change on-change'
                          :on-blur on-blur
                          :nillable true
                          :value m2}]]]))

(mf/defc margin-multiple
  {::mf/props :obj}
  [{:keys [values on-change on-blur]}]
  (let [margin (:layout-item-margin values)
        m1     (:m1 margin)
        m2     (:m2 margin)
        m3     (:m3 margin)
        m4     (:m4 margin)

        on-focus
        (mf/use-fn
         (fn [event]
           (let [attr (-> (dom/get-current-target event)
                          (dom/get-data "name")
                          (keyword))]
             (select-margin attr)
             (dom/select-target event))))

        on-change'
        (mf/use-fn
         (mf/deps on-change)
         (fn [value event]
           (let [attr (-> (dom/get-current-target event)
                          (dom/get-data "name")
                          (keyword))]
             (on-change :multiple attr value))))]

    [:div {:class (stl/css :margin-multiple)}
     [:div {:class (stl/css :top-margin)
            :title "Top margin"}
      [:span {:class (stl/css :icon)}
       i/margin-top-refactor]
      [:> numeric-input* {:class (stl/css :numeric-input)
                          :placeholder "--"
                          :data-name "m1"
                          :on-focus on-focus
                          :on-change on-change'
                          :on-blur on-blur
                          :nillable true
                          :value m1}]]
     [:div {:class (stl/css :right-margin)
            :title "Right margin"}
      [:span {:class (stl/css :icon)}
       i/margin-right-refactor]
      [:> numeric-input* {:class (stl/css :numeric-input)
                          :placeholder "--"
                          :data-name "m2"
                          :on-focus on-focus
                          :on-change on-change'
                          :on-blur on-blur
                          :nillable true
                          :value m2}]]

     [:div {:class (stl/css :bottom-margin)
            :title "Bottom margin"}
      [:span {:class (stl/css :icon)}
       i/margin-bottom-refactor]
      [:> numeric-input* {:class (stl/css :numeric-input)
                          :placeholder "--"
                          :data-name "m3"
                          :on-focus on-focus
                          :on-change on-change'
                          :on-blur on-blur
                          :nillable true
                          :value m3}]]

     [:div {:class (stl/css :left-margin)
            :title "Left margin"}
      [:span {:class (stl/css :icon)}
       i/margin-left-refactor]
      [:> numeric-input* {:class (stl/css :numeric-input)
                          :placeholder "--"
                          :data-name "m4"
                          :on-focus on-focus
                          :on-change on-change'
                          :on-blur on-blur
                          :nillable true
                          :value m4}]]]))


(mf/defc margin-section
  {::mf/props :obj
   ::mf/private true
   ::mf/expected-props #{:values :type :on-type-change :on-change}}
  [{:keys [type on-type-change] :as props}]
  (let [type       (d/nilv type :simple)
        on-blur    (mf/use-fn #(select-margins false false false false))
        props      (mf/spread-obj props {:on-blur on-blur})

        on-type-change'
        (mf/use-fn
         (mf/deps type on-type-change)
         (fn [_]
           (if (= type :multiple)
             (on-type-change :simple)
             (on-type-change :multiple))))]

    (mf/with-effect []
      (fn [] (on-blur)))

    [:div {:class (stl/css :margin-row)}
     [:div {:class (stl/css :inputs-wrapper)}
      (cond
        (= type :simple)
        [:> margin-simple props]

        (= type :multiple)
        [:> margin-multiple props])]

     [:button {:class (stl/css-case
                       :margin-mode true
                       :selected (= type :multiple))
               :title "Margin - multiple"
               :on-click on-type-change'}
      i/margin-refactor]]))

(mf/defc element-behaviour-horizontal
  {::mf/props :obj}
  [{:keys [^boolean auto ^boolean fill layout-item-sizing on-change]}]
  [:div {:class (stl/css-case :horizontal-behaviour true
                              :one-element (and (not fill) (not auto))
                              :two-element (or fill auto)
                              :three-element (and fill auto))}
   [:& radio-buttons {:selected  (d/name layout-item-sizing)
                      :on-change on-change
                      :wide      true
                      :name      "flex-behaviour-h"}
    [:& radio-button {:value "fix"
                      :icon  i/fixed-width-refactor
                      :title "Fix width"
                      :id    "behaviour-h-fix"}]
    (when fill
      [:& radio-button {:value "fill"
                        :icon  i/fill-content-refactor
                        :title "Width 100%"
                        :id    "behaviour-h-fill"}])
    (when auto
      [:& radio-button {:value "auto"
                        :icon  i/hug-content-refactor
                        :title "Fit content"
                        :id    "behaviour-h-auto"}])]])

(mf/defc element-behaviour-vertical
  {::mf/props :obj}
  [{:keys [^boolean auto ^boolean fill layout-item-sizing on-change]}]
  [:div {:class (stl/css-case :vertical-behaviour true
                              :one-element (and (not fill) (not auto))
                              :two-element (or fill auto)
                              :three-element (and fill auto))}
   [:& radio-buttons {:selected  (d/name layout-item-sizing)
                      :on-change on-change
                      :wide      true
                      :name      "flex-behaviour-v"}
    [:& radio-button {:value      "fix"
                      :icon       i/fixed-width-refactor
                      :icon-class (stl/css :rotated)
                      :title      "Fix height"
                      :id         "behaviour-v-fix"}]
    (when fill
      [:& radio-button {:value      "fill"
                        :icon       i/fill-content-refactor
                        :icon-class (stl/css :rotated)
                        :title      "Height 100%"
                        :id         "behaviour-v-fill"}])
    (when auto
      [:& radio-button {:value      "auto"
                        :icon       i/hug-content-refactor
                        :icon-class (stl/css :rotated)
                        :title      "Fit content"
                        :id         "behaviour-v-auto"}])]])

(mf/defc element-behaviour
  {::mf/props :obj}
  [{:keys [^boolean auto ^boolean fill
           layout-item-h-sizing
           layout-item-v-sizing
           on-change-behaviour-h-refactor
           on-change-behaviour-v-refactor]}]
  [:div {:class (stl/css-case :behaviour-menu true
                              :wrap (and fill auto))}
   [:& element-behaviour-horizontal {:auto auto
                                     :fill fill
                                     :layout-item-sizing layout-item-h-sizing
                                     :on-change on-change-behaviour-h-refactor}]
   [:& element-behaviour-vertical {:auto auto
                                   :fill fill
                                   :layout-item-sizing layout-item-v-sizing
                                   :on-change on-change-behaviour-v-refactor}]])

(mf/defc align-self-row
  {::mf/props :obj}
  [{:keys [^boolean is-col align-self on-change]}]
  [:& radio-buttons {:selected (d/name align-self)
                     :on-change on-change
                     :name "flex-align-self"
                     :allow-empty true}
   [:& radio-button {:value "start"
                     :icon  (get-layout-flex-icon :align-self :start is-col)
                     :title "Align self start"
                     :id     "align-self-start"}]
   [:& radio-button {:value "center"
                     :icon  (get-layout-flex-icon :align-self :center is-col)
                     :title "Align self center"
                     :id    "align-self-center"}]
   [:& radio-button {:value "end"
                     :icon  (get-layout-flex-icon :align-self :end is-col)
                     :title "Align self end"
                     :id    "align-self-end"}]])

(mf/defc layout-item-menu
  {::mf/wrap [#(mf/memo' % (mf/check-props ["ids" "values" "type" "is-layout-child?" "is-grid-parent?" "is-flex-parent?"]))]}
  [{:keys [ids values is-layout-child? is-layout-container? is-grid-parent? is-flex-parent? is-flex-layout? is-grid-layout?] :as props}]

  (let [selection-parents-ref (mf/use-memo (mf/deps ids) #(refs/parents-by-ids ids))
        selection-parents     (mf/deref selection-parents-ref)

        is-absolute?          (:layout-item-absolute values)

        is-col? (every? ctl/col? selection-parents)

        is-layout-child? (and is-layout-child? (not is-absolute?))

        state*                 (mf/use-state true)
        open?                  (deref state*)
        toggle-content         (mf/use-fn #(swap! state* not))
        has-content?           (or is-layout-child? is-flex-parent? is-grid-parent? is-layout-container?)

        ;; Align self

        align-self         (:layout-item-align-self values)

        title
        (cond
          (and is-layout-container? (not is-layout-child?) is-flex-layout?)
          "Flex board"

          (and is-layout-container? (not is-layout-child?) is-grid-layout?)
          "Grid board"

          (and is-layout-container? (not is-layout-child?))
          "Layout board"

          is-flex-parent?
          "Flex element"

          is-grid-parent?
          "Grid element"

          :else
          "Layout element")

        set-align-self
        (mf/use-fn
         (mf/deps ids align-self)
         (fn [value]
           (if (= align-self value)
             (st/emit! (dwsl/update-layout-child ids {:layout-item-align-self nil}))
             (st/emit! (dwsl/update-layout-child ids {:layout-item-align-self (keyword value)})))))

        ;; Margin

        on-change-margin-type
        (fn [type]
          (st/emit! (dwsl/update-layout-child ids {:layout-item-margin-type type})))

        on-margin-change
        (fn [type prop val]
          (cond
            (and (= type :simple) (= prop :m1))
            (st/emit! (dwsl/update-layout-child ids {:layout-item-margin {:m1 val :m3 val}}))

            (and (= type :simple) (= prop :m2))
            (st/emit! (dwsl/update-layout-child ids {:layout-item-margin {:m2 val :m4 val}}))

            :else
            (st/emit! (dwsl/update-layout-child ids {:layout-item-margin {prop val}}))))

        ;; Behaviour

        on-change-behaviour
        (mf/use-fn
         (mf/deps ids)
         (fn [event]
           (let [value (-> (dom/get-current-target event)
                           (dom/get-data "value")
                           (keyword))
                 dir (-> (dom/get-current-target event)
                         (dom/get-data "direction")
                         (keyword))]
             (if (= dir :h)
               (st/emit! (dwsl/update-layout-child ids {:layout-item-h-sizing value}))
               (st/emit! (dwsl/update-layout-child ids {:layout-item-v-sizing value}))))))

        on-change-behaviour-h
        (mf/use-fn
         (mf/deps ids)
         (fn [value]
           (let [value (keyword value)]
             (st/emit! (dwsl/update-layout-child ids {:layout-item-h-sizing value})))))


        on-change-behaviour-v
        (mf/use-fn
         (mf/deps ids)
         (fn [value]
           (let [value (keyword value)]
             (st/emit! (dwsl/update-layout-child ids {:layout-item-v-sizing value})))))

        ;; Size and position

        on-size-change
        (fn [measure value]
          (st/emit! (dwsl/update-layout-child ids {measure value})))

        on-change-position
        (mf/use-fn
         (mf/deps ids)
         (fn [value]
           (let [value (keyword value)]
             (when (= value :static)
               (st/emit! (dwsl/update-layout-child ids {:layout-item-z-index nil})))
             (st/emit! (dwsl/update-layout-child ids {:layout-item-absolute (= value :absolute)})))))

        ;; Z Index
        on-change-z-index
        (mf/use-fn
         (mf/deps ids)
         (fn [value]
           (st/emit! (dwsl/update-layout-child ids {:layout-item-z-index value}))))]

    [:div {:class (stl/css :element-set)}
     [:div {:class (stl/css :element-title)}
      [:& title-bar {:collapsable  has-content?
                     :collapsed    (not open?)
                     :on-collapsed toggle-content
                     :title        title
                     :class        (stl/css-case :title-spacing-layout-element true
                                                 :title-spacing-empty (not has-content?))}]]
     (when open?
       [:div {:class (stl/css :flex-element-menu)}
        (when (or is-layout-child? is-absolute?)
          [:div {:class (stl/css :row)}
           [:div {:class (stl/css :position-options)}
            [:& radio-buttons {:selected (if is-absolute? "absolute" "static")
                               :on-change on-change-position
                               :name "layout-style"
                               :wide true}
             [:& radio-button {:value "static"
                               :id :static-position}]
             [:& radio-button {:value "absolute"
                               :id :absolute-position}]]]

           [:div {:class (stl/css :z-index-wrapper)
                  :title "z-index"}

            [:span {:class (stl/css :icon-text)}
             "Z"]
            [:> numeric-input*
             {:className (stl/css :numeric-input)
              :placeholder "--"
              :on-focus #(dom/select-target %)
              :on-change #(on-change-z-index %)
              :nillable true
              :value (:layout-item-z-index values)}]]])

        [:div {:class (stl/css :row)}
         [:& element-behaviour {:fill is-layout-child?
                                :auto is-layout-container?
                                :layout-item-v-sizing (or (:layout-item-v-sizing values) :fix)
                                :layout-item-h-sizing (or (:layout-item-h-sizing values) :fix)
                                :on-change-behaviour-h-refactor on-change-behaviour-h
                                :on-change-behaviour-v-refactor on-change-behaviour-v
                                :on-change on-change-behaviour}]]

        (when (and is-layout-child? is-flex-parent?)
          [:div {:class (stl/css :row)}
           [:& align-self-row {:is-col is-col?
                               :align-self align-self
                               :on-change set-align-self}]])

        (when is-layout-child?
          [:div {:class (stl/css :row)}
           [:& margin-section {:values (:layout-item-margin values)
                               :type (:layout-item-margin-type values)
                               :on-type-change on-change-margin-type
                               :on-change on-margin-change}]])

        (when (or (= (:layout-item-h-sizing values) :fill)
                  (= (:layout-item-v-sizing values) :fill))
          [:div {:class (stl/css :row)}
           [:div {:class (stl/css :advanced-options)}
            (when (= (:layout-item-h-sizing values) :fill)
              [:div {:class (stl/css :horizontal-fill)}
               [:div {:class (stl/css :layout-item-min-w)
                      :title (tr "workspace.options.layout-item.layout-item-min-w")}

                [:span {:class (stl/css :icon-text)}
                 "MIN W"]
                [:> numeric-input*
                 {:className (stl/css :numeric-input)
                  :no-validate true
                  :min 0
                  :data-wrap true
                  :placeholder "--"
                  :on-focus #(dom/select-target %)
                  :on-change (partial on-size-change :layout-item-min-w)
                  :value (get values :layout-item-min-w)
                  :nillable true}]]

               [:div {:class (stl/css :layout-item-max-w)
                      :title (tr "workspace.options.layout-item.layout-item-max-w")}
                [:span {:class (stl/css :icon-text)} "MAX W"]
                [:> numeric-input*
                 {:className (stl/css :numeric-input)
                  :no-validate true
                  :min 0
                  :data-wrap true
                  :placeholder "--"
                  :on-focus #(dom/select-target %)
                  :on-change (partial on-size-change :layout-item-max-w)
                  :value (get values :layout-item-max-w)
                  :nillable true}]]])
            (when (= (:layout-item-v-sizing values) :fill)
              [:div {:class (stl/css :vertical-fill)}
               [:div {:class (stl/css :layout-item-min-h)
                      :title (tr "workspace.options.layout-item.layout-item-min-h")}

                [:span {:class (stl/css :icon-text)}
                 "MIN H"]
                [:> numeric-input*
                 {:className (stl/css :numeric-input)
                  :no-validate true
                  :min 0
                  :data-wrap true
                  :placeholder "--"
                  :on-focus #(dom/select-target %)
                  :on-change (partial on-size-change :layout-item-min-h)
                  :value (get values :layout-item-min-h)
                  :nillable true}]]

               [:div {:class (stl/css :layout-item-max-h)
                      :title (tr "workspace.options.layout-item.layout-item-max-h")}

                [:span {:class (stl/css :icon-text)}
                 "MAX H"]
                [:> numeric-input*
                 {:className (stl/css :numeric-input)
                  :no-validate true
                  :min 0
                  :data-wrap true
                  :placeholder "--"
                  :on-focus #(dom/select-target %)
                  :on-change (partial on-size-change :layout-item-max-h)
                  :value (get values :layout-item-max-h)
                  :nillable true}]]])]])])]))
