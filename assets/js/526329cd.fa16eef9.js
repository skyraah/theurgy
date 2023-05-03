"use strict";(self.webpackChunktheurgy_documentation=self.webpackChunktheurgy_documentation||[]).push([[39],{3905:(e,t,r)=>{r.d(t,{Zo:()=>l,kt:()=>g});var n=r(7294);function a(e,t,r){return t in e?Object.defineProperty(e,t,{value:r,enumerable:!0,configurable:!0,writable:!0}):e[t]=r,e}function i(e,t){var r=Object.keys(e);if(Object.getOwnPropertySymbols){var n=Object.getOwnPropertySymbols(e);t&&(n=n.filter((function(t){return Object.getOwnPropertyDescriptor(e,t).enumerable}))),r.push.apply(r,n)}return r}function o(e){for(var t=1;t<arguments.length;t++){var r=null!=arguments[t]?arguments[t]:{};t%2?i(Object(r),!0).forEach((function(t){a(e,t,r[t])})):Object.getOwnPropertyDescriptors?Object.defineProperties(e,Object.getOwnPropertyDescriptors(r)):i(Object(r)).forEach((function(t){Object.defineProperty(e,t,Object.getOwnPropertyDescriptor(r,t))}))}return e}function c(e,t){if(null==e)return{};var r,n,a=function(e,t){if(null==e)return{};var r,n,a={},i=Object.keys(e);for(n=0;n<i.length;n++)r=i[n],t.indexOf(r)>=0||(a[r]=e[r]);return a}(e,t);if(Object.getOwnPropertySymbols){var i=Object.getOwnPropertySymbols(e);for(n=0;n<i.length;n++)r=i[n],t.indexOf(r)>=0||Object.prototype.propertyIsEnumerable.call(e,r)&&(a[r]=e[r])}return a}var p=n.createContext({}),s=function(e){var t=n.useContext(p),r=t;return e&&(r="function"==typeof e?e(t):o(o({},t),e)),r},l=function(e){var t=s(e.components);return n.createElement(p.Provider,{value:t},e.children)},u="mdxType",d={inlineCode:"code",wrapper:function(e){var t=e.children;return n.createElement(n.Fragment,{},t)}},y=n.forwardRef((function(e,t){var r=e.components,a=e.mdxType,i=e.originalType,p=e.parentName,l=c(e,["components","mdxType","originalType","parentName"]),u=s(r),y=a,g=u["".concat(p,".").concat(y)]||u[y]||d[y]||i;return r?n.createElement(g,o(o({ref:t},l),{},{components:r})):n.createElement(g,o({ref:t},l))}));function g(e,t){var r=arguments,a=t&&t.mdxType;if("string"==typeof e||a){var i=r.length,o=new Array(i);o[0]=y;var c={};for(var p in t)hasOwnProperty.call(t,p)&&(c[p]=t[p]);c.originalType=e,c[u]="string"==typeof e?e:a,o[1]=c;for(var s=2;s<i;s++)o[s]=r[s];return n.createElement.apply(null,o)}return n.createElement.apply(null,r)}y.displayName="MDXCreateElement"},3807:(e,t,r)=>{r.r(t),r.d(t,{assets:()=>p,contentTitle:()=>o,default:()=>d,frontMatter:()=>i,metadata:()=>c,toc:()=>s});var n=r(7462),a=(r(7294),r(3905));const i={sidebar_position:5},o="Datagen",c={unversionedId:"recipe_types/datagen",id:"recipe_types/datagen",title:"Datagen",description:"If you are at least passingly familiar with java and minecraft modding, then the easiest way to add recipes to theurgy is to use the theurgy datagen.",source:"@site/docs/recipe_types/datagen.md",sourceDirName:"recipe_types",slug:"/recipe_types/datagen",permalink:"/theurgy/recipe_types/datagen",draft:!1,editUrl:"https://github.com/klikli-dev/theurgy/tree/documentation/docs/recipe_types/datagen.md",tags:[],version:"current",sidebarPosition:5,frontMatter:{sidebar_position:5},sidebar:"tutorialSidebar",previous:{title:"Crafting Recipes",permalink:"/theurgy/divination_rods/crafting_recipes"},next:{title:"Calcination Recipes",permalink:"/theurgy/recipe_types/calcination"}},p={},s=[],l={toc:s},u="wrapper";function d(e){let{components:t,...r}=e;return(0,a.kt)(u,(0,n.Z)({},l,r,{components:t,mdxType:"MDXLayout"}),(0,a.kt)("h1",{id:"datagen"},"Datagen"),(0,a.kt)("p",null,"If you are at least passingly familiar with java and minecraft modding, then the easiest way to add recipes to theurgy is to use the theurgy datagen.\nYou can find Recipe Providers for all Theurgy recipe types here: ",(0,a.kt)("a",{parentName:"p",href:"https://github.com/klikli-dev/theurgy/tree/version/1.19.4/src/main/java/com/klikli_dev/theurgy/datagen"},"https://github.com/klikli-dev/theurgy/tree/version/1.19.4/src/main/java/com/klikli_dev/theurgy/datagen")),(0,a.kt)("p",null,"Simply clone the theurgy repository, add your own datagen code based on the existing example code, run the gradle task ",(0,a.kt)("inlineCode",{parentName:"p"},"runData")," and copy the results from ",(0,a.kt)("inlineCode",{parentName:"p"},"src/generated/resources/...")," into your datapack."))}d.isMDXComponent=!0}}]);